package com.paulstna.user.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponseDTO  {

    private String apiPath;
    private int errorCode;
    private String errorMessage;
    private LocalDateTime errorTime;
}
