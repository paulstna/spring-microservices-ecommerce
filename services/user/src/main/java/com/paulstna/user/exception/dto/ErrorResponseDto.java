package com.paulstna.user.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private Instant timestamp;
    private Integer status;
    private String message;
}
