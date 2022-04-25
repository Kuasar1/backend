package mn.ezbuy.orderservice.controller;

import mn.ezbuy.orderservice.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/order")
public interface OrderController {

    @GetMapping("/find/{id}")
    ResponseEntity<?> getUserOrder(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @GetMapping("/")
    ResponseEntity<?> getAll(@RequestHeader(value = "Authorization") String token);

    @PostMapping("/add")
    ResponseEntity<?> add(@RequestBody Order request, @RequestHeader(value = "Authorization") String token);

    @PutMapping("/update/{id}")
    ResponseEntity<?> update(@RequestBody Order request, @RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

}
