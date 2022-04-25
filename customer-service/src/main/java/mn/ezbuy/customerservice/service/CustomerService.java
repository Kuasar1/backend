package mn.ezbuy.customerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.customerservice.entity.*;
import mn.ezbuy.customerservice.repository.CustomerRepository;
import mn.ezbuy.customerservice.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @SneakyThrows
    public ResponseEntity<?> register(RegisterRequest request) {
        log.info("Start register");
        log.debug("register REQ:{}",request);
        try {
            request.setUsername(request.getUsername().toLowerCase());
            boolean exists = customerRepository.findByUsername(request.getUsername()).isPresent();
            if(exists) {
                return new ResponseEntity<>("Customer already exists!", HttpStatus.BAD_REQUEST);
            } else {
                if(!isStrongPassword(request.getPassword())) {
                    return new ResponseEntity<>("Password does not satisfy requirements!",HttpStatus.BAD_REQUEST);
                } else {
                    Customer customer = new Customer();
                    customer.setUsername(request.getUsername());
                    customer.setFirstname(request.getFirstname());
                    customer.setLastname(request.getLastname());
                    customer.setEmail(request.getEmail());
                    customer.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
                    customer.setRoles(List.of(new Role(2,"USER")));
                    Customer createdCustomer = customerRepository.save(customer);
                    return generateResponse(createdCustomer);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End register");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> login(LoginRequest request) {
        log.info("Start login");
        log.debug("login REQ:{}",request);
        try {
            request.setUsername(request.getUsername().toLowerCase());
            boolean exists = customerRepository.findByUsername(request.getUsername()).isPresent();
            if(!exists) {
                return new ResponseEntity<>("Customer with username: " + request.getUsername() + " does not exist!", HttpStatus.BAD_REQUEST);
            } else {
                Customer customer = customerRepository.findByUsername(request.getUsername()).get();
                if(!bCryptPasswordEncoder.matches(request.getPassword(),customer.getPassword())) {
                    return new ResponseEntity<>("Password does not match!", HttpStatus.BAD_REQUEST);
                } else {
                    return generateResponse(customer);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End login");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> update(UpdateRequest request, String token, Long id) {
        log.info("Start update");
        log.debug("update REQ:{}",request);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token, id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = customerRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Customer does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Customer previousCustomer = customerRepository.getById(id);
                    if(!ObjectUtils.isEmpty(request.getPassword()) && !isStrongPassword(request.getPassword())) {
                        return new ResponseEntity<>("Password does not satisfy requirements!",HttpStatus.BAD_REQUEST);
                    } else {
                        previousCustomer.setUsername(ObjectUtils.isEmpty(request.getUsername()) ? previousCustomer.getUsername() : request.getUsername());
                        previousCustomer.setFirstname(ObjectUtils.isEmpty(request.getFirstname()) ? previousCustomer.getFirstname() : request.getFirstname());
                        previousCustomer.setLastname(ObjectUtils.isEmpty(request.getLastname()) ? previousCustomer.getLastname() : request.getLastname());
                        previousCustomer.setEmail(ObjectUtils.isEmpty(request.getEmail()) ? previousCustomer.getEmail() : request.getEmail());
                        previousCustomer.setPassword(ObjectUtils.isEmpty(request.getPassword()) ? previousCustomer.getPassword() : bCryptPasswordEncoder.encode(request.getPassword()));
                        previousCustomer.setImage(ObjectUtils.isEmpty(request.getImage()) ? previousCustomer.getImage() : request.getImage());
                        Customer updatedCustomer = customerRepository.save(previousCustomer);
                        return generateResponse(updatedCustomer);
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
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAuthorization(token, id);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = customerRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Customer does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    customerRepository.deleteById(id);
                    return new ResponseEntity<>("Customer was deleted successfully!",HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End delete");
        }
    }

    @SneakyThrows
    public ResponseEntity<?> get(String token, Long id) {
        log.info("Start get");
        log.debug("get REQ:{}",id);
        try {
            ResponseEntity<?> verificationResponse = jwtUtil.verifyTokenAndAdmin(token);
            if(verificationResponse.getStatusCode() != HttpStatus.OK) {
                return verificationResponse;
            } else {
                boolean exists = customerRepository.findById(id).isPresent();
                if(!exists) {
                    return new ResponseEntity<>("Customer does not exist!", HttpStatus.BAD_REQUEST);
                } else {
                    Customer customer = customerRepository.getById(id);
                    Response customerWithoutToken = getCustomerWithoutToken(customer);
                    return new ResponseEntity<>(customerWithoutToken,HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End get");
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
                List<Response> customersWithoutToken = new ArrayList<>();
                for (Customer customer : customerRepository.findAll()) {
                    customersWithoutToken.add(getCustomerWithoutToken(customer));
                }
                return new ResponseEntity<>(customersWithoutToken,HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End getAll");
        }
    }

    private ResponseEntity<?> generateResponse(Customer customer) {
        String accessToken = jwtUtil.generateToken(customer);
        Response response = new Response();
        response.setId(customer.getId());
        response.setUsername(customer.getUsername());
        response.setEmail(customer.getEmail());
        response.setAdmin(customer.getRoles().stream().anyMatch(role -> role.getId() == 1));
        response.setAccessToken(accessToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Response getCustomerWithoutToken(Customer customer) {
        Response response = new Response();
        response.setId(customer.getId());
        response.setUsername(customer.getUsername());
        response.setEmail(customer.getEmail());
        response.setAdmin(customer.getRoles().stream().anyMatch(role -> role.getId() == 1));
        return response;
    }

    private boolean isStrongPassword (String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])"
                + "(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
