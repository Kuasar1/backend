package mn.ezbuy.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.paymentservice.entity.ChargeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api_key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @SneakyThrows
    public ResponseEntity<?> charge(ChargeRequest request) {
        log.debug("Start charge");
        log.debug("charge REQ:{}",request);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount",request.getAmount().longValue());
            params.put("currency",request.getCurrency());
            params.put("source",request.getSource());
            Charge charge = Charge.create(params);
            Charge receive = Charge.retrieve(charge.getId());
            return new ResponseEntity<>(receive.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.debug("End charge");
        }
    }

}
