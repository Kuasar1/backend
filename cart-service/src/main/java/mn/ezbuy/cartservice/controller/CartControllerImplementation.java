package mn.ezbuy.cartservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.cartservice.entity.Cart;
import mn.ezbuy.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/cart")
public class CartControllerImplementation implements CartController {

    @Autowired
    private final CartService cartService;

    @Override
    public ResponseEntity<?> getAll(String token) {
        return cartService.getAll(token);
    }

    @Override
    public ResponseEntity<?> getUserCart(String token, Long id) {
        return cartService.getUserCart(token,id);
    }

    @Override
    public ResponseEntity<?> add(Cart request, String token) {
        return cartService.add(request,token);
    }

    @Override
    public ResponseEntity<?> update(Cart request, String token, Long id) {
        return cartService.update(request,token,id);
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        return cartService.delete(token,id);
    }
}
