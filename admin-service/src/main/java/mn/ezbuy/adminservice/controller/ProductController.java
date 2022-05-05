package mn.ezbuy.adminservice.controller;

import mn.ezbuy.adminservice.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/products")
public interface ProductController {

    @GetMapping("/")
    ResponseEntity<?> getAll();

    @GetMapping("/{id}")
    ResponseEntity<?> get(@PathVariable(value = "id") Long id);

    @GetMapping("/foruser/{id}")
    ResponseEntity<?> get(@PathVariable(value = "id") Long id, @RequestParam(value = "userId") Long userId, @RequestHeader(value = "Authorization") String token);

    @GetMapping
    ResponseEntity<?> getByCategory(@RequestParam String category);

    @PostMapping("/add")
    ResponseEntity<?> add(@RequestBody Product request, @RequestHeader(value = "Authorization") String token);

    @PutMapping("/update/{id}")
    ResponseEntity<?> update(@RequestBody Product request, @RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> delete(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

    @GetMapping("/getByName/{name}")
    ResponseEntity<?> getByName(@PathVariable(value = "name") String name);

}
