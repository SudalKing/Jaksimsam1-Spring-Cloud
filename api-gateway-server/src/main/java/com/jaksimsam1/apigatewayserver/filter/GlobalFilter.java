package com.jaksimsam1.apigatewayserver.filter;

import com.jaksimsam1.apigatewayserver.model.GlobalConfigData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalConfigData> {

    public GlobalFilter() {
        super(GlobalConfigData.class);
    }

    @Override
    public GatewayFilter apply(GlobalConfigData config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Gateway 진입 로깅
            log.info("Global Filter In: Request Id -> {}", request.getId());

            if (config.isPreLogger()) log.info("Global Filter Start: BaseMessage -> {}", config.getBaseMessage());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) log.info("Global Filter End: Response Code -> {}", response.getStatusCode());
                    }));
        });
    }
}
