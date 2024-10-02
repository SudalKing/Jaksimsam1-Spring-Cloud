package com.jaksimsam1.apigatewayserver.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class GlobalConfigData {
    private String baseMessage;
    private boolean preLogger;
    private boolean postLogger;
}
