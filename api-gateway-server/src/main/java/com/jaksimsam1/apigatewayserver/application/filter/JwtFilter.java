package com.jaksimsam1.apigatewayserver.application.filter;

import com.jaksimsam1.apigatewayserver.application.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtProvider jwtProvider;

    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !jwtProvider.validateToken(token)) {
                log.error("Invalid JWT");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            Claims claims = jwtProvider.getClaims(token);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", claims.getSubject())
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        });
    }
}
