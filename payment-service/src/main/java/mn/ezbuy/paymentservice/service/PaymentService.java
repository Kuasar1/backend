package mn.ezbuy.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.StripeObject;
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
        log.info("Start charge");
        log.info("charge REQ:{}",request);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount",request.getAmount().longValue());
            params.put("currency",request.getCurrency());
            params.put("source",request.getSource());
            Charge charge = Charge.create(params);

            Charge receive = Charge.retrieve(charge.getId());
            log.warn(receive.toJson());
            return new ResponseEntity<>(receive.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            log.info("End charge");
        }
    }

//    @SneakyThrows
//    public ResponseEntity<?> createIntent(IntentRequest request) {
//        log.info("Start createIntent");
//        log.debug("createIntent REQ:{}",request);
//        try {
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                    .setAmount(request.getAmount().longValue() * 100)
//                    .putMetadata("customer_id",String.valueOf(request.getCustomerId()))
//                    .putMetadata("order_id",String.valueOf(request.getOrderId()))
//                    .addPaymentMethodType("card")
//                    .setCurrency("usd")
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//            Map<String, Object> response = new HashMap<>();
//            response.put("clientSecret", paymentIntent.getClientSecret());
//            response.put("id",paymentIntent.getId());
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End createIntent");
//        }
//    }

//    @SneakyThrows
//    public ResponseEntity<?> payIntent(PaymentRequest request) {
//        log.info("Start payInvoice");
//        log.debug("payInvoice REQ:{}",request);
//        try {
//            PaymentIntent received = PaymentIntent.retrieve(request.getIntentId());
//
//            Map<String, Object> params = new HashMap<>();
//            params.put("payment_method", "pm_card_visa");
//            PaymentIntent updated = received.confirm(params);
//
////            Map<String, Object> response = new HashMap<>();
////            response.put("clientSecret",updated.getClientSecret());
//
//            return new ResponseEntity<>(updated, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End payInvoice");
//        }
//    }

//    @SneakyThrows
//    public Charge charge(IntentRequest request) {
//        log.info("Start charge");
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("amount", request.getAmount().longValue() * 100);
//            params.put("currency", request.getCurrency().name());
//            params.put("description", request.getDescription());
//            params.put("source", request.getStripeToken());
//            return Charge.create(params);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//            log.info("End charge");
//        }
//    }

//    @SneakyThrows
//    public StripeResponse something(IntentRequest request) {
//        PaymentIntentCreateParams.Builder paramsBuilder = new PaymentIntentCreateParams
//                .Builder()
//                .addPaymentMethodType(request.getPaymentMethodType() == null ? "card" : request.getPaymentMethodType())
//                .setCurrency(request.getCurrency().name())
//                .setAmount(request.getAmount().longValue());
//
//        PaymentIntentCreateParams createParams = paramsBuilder.build();
//
//        try {
//            PaymentIntent intent = PaymentIntent.create(createParams);
//            return new StripeResponse(200, HttpHeaders.of(null),String.valueOf(intent));
//            //return new ResponseEntity<>(intent,HttpStatus.OK);
//        } catch (Exception e) {
//            throw new Exception(e);
//        } finally {
//
//        }
//    }

}
