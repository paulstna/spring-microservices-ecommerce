package com.paulstna.inventory.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private Instant timestamp;
    private Integer status;
    private String message;
}
