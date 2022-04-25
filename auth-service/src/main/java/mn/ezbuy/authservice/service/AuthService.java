//package mn.ezbuy.authservice.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import mn.ezbuy.authservice.entity.AuthResponse;
//import mn.ezbuy.authservice.util.JwtUtil;
//import mn.ezbuy.customerservice.controller.CustomerClient;
//import mn.ezbuy.customerservice.entity.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ObjectUtils;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    @Autowired
//    private final CustomerClient customerClient;
//    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//    private final JwtUtil jwtUtil;
//
//    @SneakyThrows
//    public ResponseEntity<?> register(RegisterRequest request) {
//        log.info("Start register");
//        try {
//            ResponseEntity<?> customerServiceResponse = customerClient.register(request);
//            return handle(customerServiceResponse);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End register");
//        }
//    }
//
//    @SneakyThrows
//    public ResponseEntity<?> login(LoginRequest request) {
//        log.info("Start login");
//        try {
//            ResponseEntity<?> customerServiceResponse = customerClient.login(request);
//            return handle(customerServiceResponse);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End login");
//        }
//    }
//
//    private ResponseEntity<?> handle(ResponseEntity<?> customerServiceResponse) {
//        if(customerServiceResponse.getStatusCode() != HttpStatus.OK || ObjectUtils.isEmpty(customerServiceResponse.getBody())) {
//            return new ResponseEntity<>(customerServiceResponse.getBody(),customerServiceResponse.getStatusCode());
//        } else {
//            Customer customer = MAPPER.convertValue(customerServiceResponse.getBody(), Customer.class);
//            String accessToken = jwtUtil.generateToken(customer);
//            AuthResponse response = new AuthResponse();
//            response.setId(customer.getId());
//            response.setUsername(customer.getUsername());
//            response.setEmail(customer.getEmail());
//            response.setAdmin(customer.getRoles().stream().anyMatch(role -> role.getId() == 1));
//            response.setAccessToken(accessToken);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
//    }
//
//    @SneakyThrows
//    public ResponseEntity<?> update(UpdateRequest request, String token, Long id) {
//        log.info("Start update");
//        try {
//            ResponseEntity<?> verificationResponse = jwtUtil.verifyToken(token, id);
//            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
//                return verificationResponse;
//            } else {
//                ResponseEntity<?> customerServiceResponse = customerClient.update(request, id);
//                return handle(customerServiceResponse);
//            }
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End update");
//        }
//    }
//
//    @SneakyThrows
//    public ResponseEntity<?> delete(String token, Long id) {
//        log.info("Start delete");
//        try {
//            ResponseEntity<?> verificationResponse = jwtUtil.verifyToken(token, id);
//            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
//                return verificationResponse;
//            } else {
//                return customerClient.delete(id);
//            }
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End delete");
//        }
//    }
//
//}
