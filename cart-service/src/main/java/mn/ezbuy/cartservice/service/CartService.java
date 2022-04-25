package mn.ezbuy.cartservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.cartservice.entity.Cart;
import mn.ezbuy.cartservice.repository.CartRepository;
import mn.ezbuy.cartservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    @Autowired
    private final CartRepository cartRepository;
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final JwtUtil jwtUtil;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public ResponseEntity<?> add(Cart request, String token) {
        log.info("Start add");
        log.debug("add REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyToken(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                Claims claims = MAPPER.convertValue(verificationResponse.getBody(),Claims.class);
                request.setUserId(claims.get("id", Long.class));
                Cart createdCart = cartRepository.save(request);
                return new ResponseEntity<>(createdCart, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End add");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> update(Cart request, String token, Long id) {
        log.info("Start update");
        log.debug("update REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = cartRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Cart does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Cart previousCart = cartRepository.getById(id);
                    if(!Objects.equals(previousCart.getUserId(), request.getUserId())) {
                        return new ResponseEntity<>("User id must not change!", HttpStatus.BAD_REQUEST);
                    } else {
                        Cart updatedCart = cartRepository.save(request);
                        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
                    }
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
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = cartRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Cart does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    cartRepository.deleteById(id);
                    return new ResponseEntity<>("Cart was deleted successfully!", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End delete");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getUserCart(String token, Long id) {
        log.info("Start getUserCart");
        log.debug("getUserCart REQ:{}",id);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = cartRepository.findCartByUserId(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Customer cart not found!",HttpStatus.BAD_REQUEST);
                } else {
                    Cart userCart = cartRepository.findCartByUserId(id).get();
                    return new ResponseEntity<>(userCart,HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getUserCart");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getAll(String token) {
        log.info("Start getAll");
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                List<Cart> carts = cartRepository.findAll();
                return new ResponseEntity<>(carts,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getAll");
        }
    }

}
