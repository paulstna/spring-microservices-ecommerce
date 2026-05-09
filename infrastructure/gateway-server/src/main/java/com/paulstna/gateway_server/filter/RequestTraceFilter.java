package com.paulstna.gateway_server.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.paulstna.gateway_server.constants.GatewayConstants.CORRELATION_ID_HEADER;

@Order(1)
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestTraceFilter implements GlobalFilter {

    private final FilterUtility filterUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (!isCorrelationIdPresent(headers)) {
            String correlationId = filterUtil.generateCorrelationId();
            exchange = filterUtil.setCorrelationId(exchange, correlationId);
            log.debug("{} generated in RequestTraceFilter: {}",
                    CORRELATION_ID_HEADER, correlationId);
        } else {
            log.debug("{} generated found in RequestTraceFilter: {}",
                    CORRELATION_ID_HEADER, headers.getFirst(CORRELATION_ID_HEADER));
        }

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders headers) {
        return filterUtil.getCorrelationId(headers) != null;
    }

}
