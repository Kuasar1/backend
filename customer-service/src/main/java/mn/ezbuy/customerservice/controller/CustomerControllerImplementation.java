package mn.ezbuy.customerservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.customerservice.entity.LoginRequest;
import mn.ezbuy.customerservice.entity.RegisterRequest;
import mn.ezbuy.customerservice.entity.UpdateRequest;
import mn.ezbuy.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/customers")
public class CustomerControllerImplementation implements CustomerController {

    @Autowired
    private final CustomerService customerService;

    @Override
    public ResponseEntity<?> getAll(String token) {
        return customerService.getAll(token);
    }

    @Override
    public ResponseEntity<?> get(String token, Long id) {
        return customerService.get(token, id);
    }

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        return customerService.register(request);
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        return customerService.login(request);
    }

    @Override
    public ResponseEntity<?> update(UpdateRequest request, String token, Long id) {
        return customerService.update(request, token, id);
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        return customerService.delete(token, id);
    }
}
