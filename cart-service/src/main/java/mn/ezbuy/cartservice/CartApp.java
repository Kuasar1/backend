package mn.ezbuy.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CartApp {

    public static void main(String[] args) {
        SpringApplication.run(CartApp.class, args);
    }

}
