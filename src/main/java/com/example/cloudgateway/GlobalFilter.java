package com.example.cloudgateway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    @Data
    public static class Config {
        private String baseMessage;
        boolean preLogger;
        boolean postLogger;
    }

    public GlobalFilter() {
        super(GlobalFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(GlobalFilter.Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("global filter base message -> {}", request.getId());

            if (config.isPreLogger()) {
                log.info("global filter : request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                if (config.isPostLogger()) {
                    log.info("global filter : response code -> {}", response.getStatusCode());
                }
            }));
        };
    }
}
