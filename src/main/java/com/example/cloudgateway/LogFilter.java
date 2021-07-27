package com.example.cloudgateway;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LogFilter extends AbstractGatewayFilterFactory<LogFilter.Config> {

    @Data
    public static class Config {
        private String baseMessage;
        boolean preLogger;
        boolean postLogger;
    }

    public LogFilter() {
        super(LogFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(LogFilter.Config config) {
//        return (exchange, chain) -> {
//
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("log filter base message -> {}", request.getId());
//
//            if (config.isPreLogger()) {
//                log.info("log filter : request id -> {}", request.getId());
//            }
//
//            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
//                if (config.isPostLogger()) {
//                    log.info("log filter : response code -> {}", response.getStatusCode());
//                }
//            }));
//        };

        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain)-> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("log filter base message -> {}", request.getId());

            if (config.isPreLogger()) {
                log.info("log filter : request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                if (config.isPostLogger()) {
                    log.info("log filter : response code -> {}", response.getStatusCode());
                }
            }));

        }, Ordered.LOWEST_PRECEDENCE);
        return filter;
    }
}
