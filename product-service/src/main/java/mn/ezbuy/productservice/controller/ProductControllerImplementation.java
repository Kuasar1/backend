package mn.ezbuy.productservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.productservice.entity.Product;
import mn.ezbuy.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/products")
public class ProductControllerImplementation implements ProductController {

    @Autowired
    private final ProductService productService;

    @Override
    public ResponseEntity<?> getAll() {
        return productService.getAll();
    }

    @Override
    public ResponseEntity<?> get(Long id, Long userId, String token) {
        return productService.getForUser(id,userId,token);
    }

    @Override
    public ResponseEntity<?> get(Long id) {
        return productService.get(id);
    }

    @Override
    public ResponseEntity<?> getByCategory(String category) {
        return productService.getByCategory(category);
    }

    @Override
    public ResponseEntity<?> add(Product request, String token) {
        return productService.add(request, token);
    }

    @Override
    public ResponseEntity<?> update(Product request, String token, Long id) {
        return productService.update(request, token, id);
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        return productService.delete(token, id);
    }

    @Override
    public ResponseEntity<?> getByName(String name) {
        return productService.getByName(name);
    }

}
