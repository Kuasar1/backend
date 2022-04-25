package mn.ezbuy.customerservice.controller;

import mn.ezbuy.customerservice.entity.LoginRequest;
import mn.ezbuy.customerservice.entity.RegisterRequest;
import mn.ezbuy.customerservice.entity.UpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/customers")
public interface CustomerController {

    @GetMapping("/")
    ResponseEntity<?> getAll(@RequestHeader(value = "Authorization") String token);

    @GetMapping("/{id}")
    ResponseEntity<?> get(@RequestHeader(value = "Authorization") String token, @PathVariable("id") Long id);

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest request);

    @PutMapping("/update/{id}")
    ResponseEntity<?> update(@RequestBody UpdateRequest request, @RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@RequestHeader(value = "Authorization") String token, @PathVariable("id") Long id);
}
