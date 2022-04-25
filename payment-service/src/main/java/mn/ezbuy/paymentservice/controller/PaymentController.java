package mn.ezbuy.paymentservice.controller;

import mn.ezbuy.paymentservice.entity.ChargeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/payment")
public interface PaymentController {

    @PostMapping("/charge")
    ResponseEntity<?> charge(@RequestBody ChargeRequest request);

}
