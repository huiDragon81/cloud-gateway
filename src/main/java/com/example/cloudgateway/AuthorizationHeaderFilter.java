package com.example.cloudgateway;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    Environment env;

    public static class Config {}

    public AuthorizationHeaderFilter() {
        super(AuthorizationHeaderFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, filterChain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "jwt is not valid", HttpStatus.UNAUTHORIZED);
            }

            return filterChain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange,String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }

    boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        String subject = null;
        String secret = env.getProperty("token.secret");

        try {
            subject = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception exception) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }
        return returnValue;
    }
}
