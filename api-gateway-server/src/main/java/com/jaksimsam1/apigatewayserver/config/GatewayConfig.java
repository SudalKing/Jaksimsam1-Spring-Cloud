package com.jaksimsam1.apigatewayserver.config;

import com.jaksimsam1.apigatewayserver.application.util.ServiceName;
import com.jaksimsam1.apigatewayserver.application.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
@Configuration
public class GatewayConfig {

    private final JwtFilter jwtFilter;

    @Value("${service.user.url}")
    private String USER_SERVICE_URL;
    @Value("${service.auth.url}")
    private String AUTH_SERVICE_URL;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service
                // 회원가입 필터 검사 x
                .route(ServiceName.USER_SERVICE, r -> r.path("/user/api/v1/register")
                        .and()
                        .method(HttpMethod.POST)
                        .uri(USER_SERVICE_URL))

                .route(ServiceName.USER_SERVICE, r -> r.path("/user/api/v1/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST)
                        .filters(f -> f
                                .removeRequestHeader("Cookie")
//                                .rewritePath("/user/api/v1(?<segment>/?.*)", "${segment}")
                                .filter(jwtFilter.apply(new Object()))
                        )
                        .uri(USER_SERVICE_URL))

                // Auth Service
                // 로그인 필터 검사 x
                .route(ServiceName.AUTH_SERVICE, r -> r.path("/auth/api/v1/login")
                        .and()
                        .method(HttpMethod.POST)
                        .uri(AUTH_SERVICE_URL))

                .route(ServiceName.AUTH_SERVICE, r -> r.path("/auth/api/v1/**")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f
                                .removeRequestHeader("Cookie")
                                .filter(jwtFilter.apply(new Object())))
                        .uri(AUTH_SERVICE_URL))

                .build();
    }
}
