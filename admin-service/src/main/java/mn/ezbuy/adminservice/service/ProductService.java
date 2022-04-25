package mn.ezbuy.adminservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.adminservice.entity.Product;
import mn.ezbuy.adminservice.repository.ProductRepository;
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
                return new ResponseEntity<>(product,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End get");
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
    public ResponseEntity<?> addRating(Object request, String token) {
        log.info("Start addRating");
        log.warn("addRating REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                System.out.println("Rating:" + request.toString());
                return new ResponseEntity<>("Success",HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End add");
        }
    }

}
