package com.paulstna.cart.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private String apiPath;
    private Integer errorCode;
    private String errorMessage;
    private LocalDateTime errorTime;
}
