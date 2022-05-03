package mn.ezbuy.adminservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.adminservice.entity.Like;
import mn.ezbuy.adminservice.entity.Product;
import mn.ezbuy.adminservice.entity.Ranking;
import mn.ezbuy.adminservice.entity.Rating;
import mn.ezbuy.adminservice.repository.LikeRepository;
import mn.ezbuy.adminservice.repository.ProductRepository;
import mn.ezbuy.adminservice.repository.RankingRepository;
import mn.ezbuy.adminservice.repository.RatingRepository;
import mn.ezbuy.adminservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final LikeRepository likeRepository;
    @Autowired
    private final RatingRepository ratingRepository;
    @Autowired
    private final RankingRepository rankingRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> add(Product request, String token) {
        log.info("Start add");
        log.debug("add REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                request.setTitle(request.getTitle().toLowerCase());
                boolean exists = productRepository.findProductByTitle(request.getTitle()).isPresent();
                if(exists) {
                    return new ResponseEntity<>("Product with title: " + request.getTitle() + " already exists!", HttpStatus.BAD_REQUEST);
                } else {
                    Product createdProduct = productRepository.save(request);
                    return new ResponseEntity<>(createdProduct,HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End add");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> update(Product request, String token, Long id) {
        log.info("Start update");
        log.debug("update REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = productRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Product does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Product previousProduct = productRepository.getById(id);
                    previousProduct.setTitle(ObjectUtils.isEmpty(request.getTitle()) ? previousProduct.getTitle() : request.getTitle());
                    previousProduct.setDescription(ObjectUtils.isEmpty(request.getDescription()) ? previousProduct.getDescription() : request.getDescription());
                    previousProduct.setImage(ObjectUtils.isEmpty(request.getImage()) ? previousProduct.getImage() : request.getImage());
                    previousProduct.setCategories(ObjectUtils.isEmpty(request.getCategories()) ? previousProduct.getCategories() : request.getCategories());
                    previousProduct.setSize(ObjectUtils.isEmpty(request.getSize()) ? previousProduct.getSize() : request.getSize());
                    previousProduct.setColor(ObjectUtils.isEmpty(request.getColor()) ? previousProduct.getColor() : request.getColor());
                    previousProduct.setPrice(request.getPrice() == null ? previousProduct.getPrice() : request.getPrice());
                    previousProduct.setIsInStock(request.getIsInStock() == null ? previousProduct.getIsInStock() : request.getIsInStock());
                    Product updatedProduct = productRepository.save(previousProduct);
                    return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End update");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> delete(String token, Long id) {
        log.info("Start delete");
        log.debug("delete REQ:{}",id);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = productRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Product does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    productRepository.deleteById(id);
                    return new ResponseEntity<>("Product was deleted successfully!",HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End delete");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> get(Long id) {
        log.info("Start get");
        log.debug("get REQ:{}",id);
        try {
            boolean exists = productRepository.findById(id).isPresent();
            if(!exists) {
                return new ResponseEntity<>("Product does not exist!", HttpStatus.BAD_REQUEST);
            } else {
                Product product = productRepository.getById(id);
//                List<Rating> rating = ratingRepository.getRatingsByProductId(product.getId());
//                if(!rating.isEmpty()) {
//                    long totalRating = 0L;
//                    int numOfRating = 0;
//                    for(Rating r : rating) {
//                        numOfRating += 1;
//                        totalRating += r.getRating();
//                    }
//                    product.setRating(Math.toIntExact(Math.round(Math.floor(totalRating/numOfRating))));
//                } else {
//                    product.setRating(0);
//                }
                product.setRating(0);
                return new ResponseEntity<>(product,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End get");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getForUser(Long id, Long userId, String token) {
        log.info("Start getForUser");
        log.warn("getForUser REQ:{}",id);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,userId);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = productRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Product does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Product product = productRepository.getById(id);
                    boolean hasRated = ratingRepository.getRatingForUser(userId,id).isPresent();
                    product.setRating(hasRated ? ratingRepository.getRatingForUser(userId,id).get().getRating() : 0);
                    return new ResponseEntity<>(product,HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getForUser");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getAll() {
        log.info("Start getAll");
        try {
            List<Product> products = productRepository.findAll();
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getAll");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getByCategory(String category) {
        log.info("Start getByCategory");
        try {
            List<Product> products = productRepository.findByCategory(category);
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getByCategory");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getByName(String name) {
        log.info("Start getByName");
        try {
            List<Product> products = productRepository.findByTitle(name);
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getByName");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> handleLike(Like request, String token) {
        log.info("Start handleLike");
        log.warn("handleLike REQ:{}",request);
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
        log.warn("addRating REQ:{}",request);
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