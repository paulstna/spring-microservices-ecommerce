package com.paulstna.gateway_server.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import static com.paulstna.gateway_server.constants.GatewayConstants.CORRELATION_ID_HEADER;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResponseTraceFilter {

    private final FilterUtility filterUtility;

    @Bean
    public GlobalFilter postGlobalFilter() {

        return (exchange, chain) ->
                chain.filter(exchange)
                        .then(Mono.fromRunnable(() -> {
                            HttpHeaders headers = exchange.getRequest().getHeaders();
                            String correlationId =
                                    filterUtility.getCorrelationId(headers);
                            log.debug(
                                    "Updated correlation id in outbound headers: {}",
                                    correlationId
                            );
                            exchange.getRequest()
                                    .mutate()
                                    .header(CORRELATION_ID_HEADER, correlationId)
                                    .build();
                        }));
    }
}
