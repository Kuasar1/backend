package mn.ezbuy.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.orderservice.entity.Order;
import mn.ezbuy.orderservice.repository.OrderRepository;
import mn.ezbuy.orderservice.util.JwtUtil;
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
public class OrderService {

    @Autowired
    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> add(Order request, String token) {
        log.debug("Start add");
        log.debug("add REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyToken(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                Order createdCart = orderRepository.save(request);
                return new ResponseEntity<>(createdCart, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End add");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> update(Order request, String token, Long id) {
        log.debug("Start update");
        log.debug("update REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = orderRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Order does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Order previousCart = orderRepository.getById(id);
                    if(!Objects.equals(previousCart.getUserId(), request.getUserId())) {
                        return new ResponseEntity<>("User id must not change!", HttpStatus.BAD_REQUEST);
                    } else {
                        Order updatedCart = orderRepository.save(request);
                        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
                    }
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
                boolean exists = orderRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Order does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    orderRepository.deleteById(id);
                    return new ResponseEntity<>("Order was deleted successfully!", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End delete");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getUserOrder(String token, Long id) {
        log.debug("Start getUserOrder");
        log.debug("getUserOrder REQ:{}",id);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token,id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = orderRepository.findOrderByUserId(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Customer order not found!",HttpStatus.BAD_REQUEST);
                } else {
                    Order userCart = orderRepository.findOrderByUserId(id).get();
                    return new ResponseEntity<>(userCart,HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End getUserOrder");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> getAll(String token) {
        log.debug("Start getAll");
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                List<Order> carts = orderRepository.findAll();
                return new ResponseEntity<>(carts,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End getAll");
        }
    }
}
