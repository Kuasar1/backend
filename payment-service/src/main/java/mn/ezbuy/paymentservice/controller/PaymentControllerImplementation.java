package mn.ezbuy.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.paymentservice.entity.ChargeRequest;
import mn.ezbuy.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/payment")
public class PaymentControllerImplementation implements PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<?> charge(ChargeRequest request) {
        return paymentService.charge(request);
    }
}
