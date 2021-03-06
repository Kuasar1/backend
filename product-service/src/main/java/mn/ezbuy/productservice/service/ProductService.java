package mn.ezbuy.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.productservice.entity.Product;
import mn.ezbuy.productservice.repository.ProductRepository;
import mn.ezbuy.productservice.repository.RatingRepository;
import mn.ezbuy.productservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final RatingRepository ratingRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> add(Product request, String token) {
        log.debug("Start add");
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
            log.debug("End add");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> update(Product request, String token, Long id) {
        log.debug("Start update");
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
            log.debug("End update");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> delete(String token, Long id) {
        log.debug("Start delete");
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
            log.debug("End delete");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> get(Long id) {
        log.debug("Start get");
        log.debug("get REQ:{}",id);
        try {
            boolean exists = productRepository.findById(id).isPresent();
            if(!exists) {
                return new ResponseEntity<>("Product does not exist!", HttpStatus.BAD_REQUEST);
            } else {
                Product product = productRepository.getById(id);
                product.setRating(0);
                return new ResponseEntity<>(product,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End get");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getForUser(Long id, Long userId, String token) {
        log.debug("Start getForUser");
        log.debug("getForUser REQ:{}",id);
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
            log.debug("End getForUser");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getAll() {
        log.debug("Start getAll");
        try {
            List<Product> products = productRepository.findAll();
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End getAll");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getByCategory(String category) {
        log.debug("Start getByCategory");
        log.debug("getByCategory REQ:{}",category);
        try {
            List<Product> products = productRepository.findByCategory(category);
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End getByCategory");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getByName(String name) {
        log.debug("Start getByName");
        log.debug("getByName REQ:{}",name);
        try {
            List<Product> products = productRepository.findByTitle(name);
            return new ResponseEntity<>(products,HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End getByName");
        }
    }

}
