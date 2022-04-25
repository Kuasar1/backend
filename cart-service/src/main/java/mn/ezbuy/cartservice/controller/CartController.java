package mn.ezbuy.cartservice.controller;

import mn.ezbuy.cartservice.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cart")
public interface CartController {

    @GetMapping("/find/{id}")
    ResponseEntity<?> getUserCart(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @GetMapping("/")
    ResponseEntity<?> getAll(@RequestHeader(value = "Authorization") String token);

    @PostMapping("/add")
    ResponseEntity<?> add(@RequestBody Cart request, @RequestHeader(value = "Authorization") String token);

    @PutMapping("/update/{id}")
    ResponseEntity<?> update(@RequestBody Cart request, @RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

}
