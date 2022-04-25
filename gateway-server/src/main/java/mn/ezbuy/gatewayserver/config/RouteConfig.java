package mn.ezbuy.gatewayserver.config;

import mn.ezbuy.gatewayserver.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@EnableHystrix
public class RouteConfig {

    @Autowired
    AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("admin-service", r -> r.path("/products/**","/admin/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://admin-service"))

                .route("cart-service", r -> r.path("/cart/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://cart-service"))

                .route("customer-service", r -> r.path("/customers/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://customer-service"))

                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://order-service"))

                .route("payment-service", r -> r.path("/payment/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://payment-service"))

                .build();
    }

}
