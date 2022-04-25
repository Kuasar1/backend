package mn.ezbuy.gatewayserver.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints =
            List.of(
                    "/customers/register",
                    "/customers/login",
                    "/products/"
            );

    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isQuery =
            serverHttpRequest -> serverHttpRequest
                    .getQueryParams()
                    .containsKey("category");

}
