package com.jaksimsam1.apigatewayserver.application.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // Gateway 진입 로깅
        log.info("Global Filter In: Request Id -> {}", request.getId());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("Global Filter Out: Request Id, Status -> {} / {}", request.getId(), response.getStatusCode());
                }));
    }

    // 가장 먼저 실행
    @Override
    public int getOrder() {
        return -1;
    }
}
