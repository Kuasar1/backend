package mn.ezbuy.recommendationservice.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.recommendationservice.entity.Like;
import mn.ezbuy.recommendationservice.entity.Product;
import mn.ezbuy.recommendationservice.entity.Ranking;
import mn.ezbuy.recommendationservice.entity.Rating;
import mn.ezbuy.recommendationservice.repository.LikeRepository;
import mn.ezbuy.recommendationservice.repository.ProductRepository;
import mn.ezbuy.recommendationservice.repository.RankingRepository;
import mn.ezbuy.recommendationservice.repository.RatingRepository;
import mn.ezbuy.recommendationservice.util.JwtUtil;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final RatingRepository ratingRepository;
    @Autowired
    private final RankingRepository rankingRepository;
    @Autowired
    private final LikeRepository likeRepository;

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> recommend(String recommendationType, Long userId, String token) {
        log.info("Start recommend");
        log.debug("recommend REQ:{} | {}",recommendationType,userId);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,userId);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                MysqlDataSource dbsource = new MysqlDataSource();
                dbsource.setUser("root");
                dbsource.setPassword("password");
                dbsource.setServerName("localhost");
                dbsource.setDatabaseName("main");

                DataModel model = new MySQLJDBCDataModel(dbsource,
                        "ratings",
                        "user_id",
                        "product_id",
                        "rating",
                        "timestamp");

                switch (recommendationType) {
                    case "JustForYou":
                        return justForYou(model,userId);
                    case "YouMayLike":
                        return youMayLike(model,userId);
                    default:
                        case "MostPopular":
                            return mostPopular();
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End recommend");
        }
    }

    @SneakyThrows
    private ResponseEntity<?> mostPopular() {
        log.info("Start mostPopular");
        try {
            List<Ranking> rankings = rankingRepository.findAll();
            rankings.sort((o1, o2) -> (int) (o2.getAverage() - o1.getAverage()));
            List<Product> products = new ArrayList<>();
            rankings.forEach(r -> products.add(productRepository.findById(r.getProductId()).get()));
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End mostPopular");
        }
    }

    @SneakyThrows
    private ResponseEntity<?> justForYou(DataModel model, Long userId) {
        log.info("Start justForYou");
        try {
            List<Product> recommendedProducts = new ArrayList<>();
            List<Rating> topRatings = ratingRepository.getTopRatingsForUser(userId);
            List<Long> topProducts = new ArrayList<>();
            topRatings.forEach(r -> {
                if (topProducts.size() < 5) {
                    topProducts.add(r.getProductId());
                }
            });
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
            for (Long productId : topProducts) {
                List<RecommendedItem> recommendations = recommender.mostSimilarItems(productId, 5);
                for (RecommendedItem recommendation : recommendations) {
                    if(recommendation.getValue() > 0.24) {
                        recommendedProducts.add(productRepository.findById(recommendation.getItemID()).get());
                    }
                }
            }
            if(recommendedProducts.isEmpty()) {
                return mostPopular();
            } else {
                return new ResponseEntity<>(recommendedProducts,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End justForYou");
        }
    }

    @SneakyThrows
    private ResponseEntity<?> youMayLike(DataModel model, Long userId) {
        log.info("Start youMayLike");
        try {
            PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5,similarity,model,1.0);
            UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(model,neighborhood,similarity);
            List<RecommendedItem> recommendedItems = userBasedRecommender.recommend(userId,40);
            log.debug("Recommendations for Customer:{} are:{}",userId,recommendedItems);
            List<Product> recommendedProducts = new ArrayList<>();
            for (RecommendedItem item : recommendedItems) {
                float estimatedPreference = userBasedRecommender.estimatePreference(userId,item.getItemID());
                if(estimatedPreference > 3.4) {
                    Product product = productRepository.findById(item.getItemID()).get();
                    log.debug("Product:{}",product);
                    recommendedProducts.add(product);
                }
            }
            if(recommendedProducts.isEmpty()) {
                return mostPopular();
            } else {
                return new ResponseEntity<>(recommendedProducts,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End youMayLike");
        }
    }


    @SneakyThrows
    public ResponseEntity<?> handleLike(Like request, String token) {
        log.info("Start handleLike");
        log.debug("handleLike REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token, request.getUserId());
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = likeRepository.getLikeForUser(request.getUserId(), request.getProductId()).isPresent();
                if (exists) {
                    Like like = likeRepository.getLikeForUser(request.getUserId(),request.getProductId()).get();
                    likeRepository.delete(like);
                } else {
                    likeRepository.save(request);
                }
                return new ResponseEntity<>("Success",HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End handleLike");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> addRating(Rating request, String token) {
        log.info("Start addRating");
        log.debug("addRating REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token, request.getUserId());
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                ratingRepository.save(request);
                Ranking ranking;
                boolean exists = rankingRepository.getRankingByProductId(request.getProductId()).isPresent();
                if (exists) {
                    ranking = rankingRepository.getRankingByProductId(request.getProductId()).get();
                    ranking.setFrequency(ranking.getFrequency() + 1);
                    ranking.setTotal(ranking.getTotal() + request.getRating());
                    ranking.setAverage(ranking.getTotal()/ranking.getFrequency());
                } else {
                    ranking = new Ranking();
                    ranking.setProductId(request.getProductId());
                    ranking.setFrequency(1L);
                    ranking.setTotal((long) request.getRating());
                    ranking.setAverage(ranking.getTotal()/ranking.getFrequency());
                }
                rankingRepository.save(ranking);
                return new ResponseEntity<>("Success",HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End addRating");
        }
    }
}
