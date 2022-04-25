package mn.ezbuy.orderservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.orderservice.entity.Order;
import mn.ezbuy.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/order")
public class OrderControllerImplementation implements OrderController {

    @Autowired
    private final OrderService orderService;

    @Override
    public ResponseEntity<?> getUserOrder(String token, Long id) {
        return orderService.getUserOrder(token,id);
    }

    @Override
    public ResponseEntity<?> getAll(String token) {
        return orderService.getAll(token);
    }

    @Override
    public ResponseEntity<?> add(Order request, String token) {
        return orderService.add(request,token);
    }

    @Override
    public ResponseEntity<?> update(Order request, String token, Long id) {
        return orderService.update(request,token,id);
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        return orderService.delete(token,id);
    }
}
