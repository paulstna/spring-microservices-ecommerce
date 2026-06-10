package com.paulstna.inventory.common.exception.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    private String apiPath;
    private int errorCode;
    private String errorMessage;
    private LocalDateTime errorTime;
}