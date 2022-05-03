package mn.ezbuy.adminservice.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.adminservice.entity.Product;
import mn.ezbuy.adminservice.entity.Ranking;
import mn.ezbuy.adminservice.entity.Rating;
import mn.ezbuy.adminservice.repository.LikeRepository;
import mn.ezbuy.adminservice.repository.ProductRepository;
import mn.ezbuy.adminservice.repository.RankingRepository;
import mn.ezbuy.adminservice.repository.RatingRepository;
import mn.ezbuy.adminservice.util.JwtUtil;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final LikeRepository likeRepository;
    @Autowired
    private final RatingRepository ratingRepository;
    @Autowired
    private final RankingRepository rankingRepository;
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> recommend(String recommendationType, Long userId, String token) {
        log.info("Start recommend");
        log.warn("recommend REQ:{} | {}",recommendationType,userId);
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
//            List<Rating> topRatings = ratingRepository.getTopRatingsForUser(userId);
//            List<Long> topProducts = new ArrayList<>();
//            topRatings.forEach(r -> {
//                if (topProducts.size() < 5) {
//                    topProducts.add(r.getProductId());
//                }
//            });
//            log.warn("Top products for USER:{} | {}",userId,topProducts);
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
            for (LongPrimitiveIterator items = model.getItemIDs(); items.hasNext();) {
                long itemId = items.nextLong();
                List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 5);
                for (RecommendedItem recommendation : recommendations) {
                    System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
                    if(recommendation.getValue() > 0.24) {
                        recommendedProducts.add(productRepository.findById(recommendation.getItemID()).get());
                    }
                }

            }
//            for(Long i : topProducts) {
//                List<RecommendedItem> recommendations = recommender.mostSimilarItems(i,5);
//                recommendations.forEach(r -> {
//                    log.warn("Recommendation: {}",r);
//                    if(r.getValue() > 0.5) {
//                        recommendedProducts.add(productRepository.findById(r.getItemID()).get());
//                    }
//                });
//            }

//            ItemBasedRecommender recommender = new GenericItemBasedRecommender(model,similarity);
//            List<RecommendedItem> recommendedItems = recommender.recommend(userId,40);
//            log.info("Recommendations for Customer:{} are:{}",userId,recommendedItems);
//            List<Product> recommendedProducts = new ArrayList<>();
//            for (RecommendedItem item : recommendedItems) {
//                float estimatedPreference = recommender.estimatePreference(userId,item.getItemID());
//                if(estimatedPreference > 3.4) {
//                    Product product = productRepository.findById(item.getItemID()).get();
//                    log.info("Product:{}",product);
//                    recommendedProducts.add(product);
//                }
//            }
            if(recommendedProducts.isEmpty()) {
                return mostPopular();
            } else {
                return new ResponseEntity<>(recommendedProducts,HttpStatus.OK);
            }
//            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
//            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(3.0,similarity,model);
//            log.info("Neighbors of Customer:{} are: {}",userId,Arrays.toString(neighborhood.getUserNeighborhood(userId)));
//            UserBasedRecommender recommender = new GenericUserBasedRecommender(model,neighborhood,similarity);
//            List<RecommendedItem> recommendedItems = recommender.recommend(userId,40);
//            log.info("Recommendations for Customer:{} are:{}",userId,recommendedItems);
//            List<Product> recommendedProducts = new ArrayList<>();
//            for (RecommendedItem item : recommendedItems) {
//                float estimatedPreference = recommender.estimatePreference(userId,item.getItemID());
//                if(estimatedPreference > 3.4) {
//                    Product product = productRepository.findById(item.getItemID()).get();
//                    log.info("Product:{}",product);
//                    recommendedProducts.add(product);
//                }
//            }
//            if(recommendedProducts.isEmpty()) {
//                return mostPopular();
//            } else {
//                return new ResponseEntity<>(recommendedProducts,HttpStatus.OK);
//            }
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
            long[] neighbors = neighborhood.getUserNeighborhood(userId);
            for (long n : neighbors) {
                System.out.println("Neighbor: " + n);
            }
            UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(model,neighborhood,similarity);
            List<RecommendedItem> recommendedItems = userBasedRecommender.recommend(userId,40);
            log.info("Recommendations for Customer:{} are:{}",userId,recommendedItems);
            List<Product> recommendedProducts = new ArrayList<>();
            for (RecommendedItem item : recommendedItems) {
                float estimatedPreference = userBasedRecommender.estimatePreference(userId,item.getItemID());
                if(estimatedPreference > 3.4) {
                    Product product = productRepository.findById(item.getItemID()).get();
                    log.info("Product:{}",product);
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

}
