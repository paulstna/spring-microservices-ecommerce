package com.paulstna.gateway_server.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.UUID;

import static com.paulstna.gateway_server.constants.GatewayConstants.CORRELATION_ID_HEADER;

@Component
public class FilterUtility {

    public String getCorrelationId(HttpHeaders requestHeaders) {
        List<String> headerValues = requestHeaders.get(CORRELATION_ID_HEADER);
        if (headerValues == null || headerValues.isEmpty()) {
            return null;
        }

        return headerValues.getFirst();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String value) {
        return exchange.mutate()
                .request(
                        exchange.getRequest()
                                .mutate()
                                .header(CORRELATION_ID_HEADER, value)
                                .build()
                )
                .build();
    }

    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
