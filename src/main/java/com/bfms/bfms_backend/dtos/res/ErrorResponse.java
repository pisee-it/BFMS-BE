package com.bfms.bfms_backend.dtos.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors) {
    
    public ErrorResponse(int status, String code, String message) {
        this(status, code, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(int status, String code, String message, Map<String, String> errors) {
        this(status, code, message, LocalDateTime.now(), errors);
    }
}
