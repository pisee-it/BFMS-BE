package com.bfms.bfms_backend.dtos.res;

import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> errors) {
    public ErrorResponse(int status, String message) {
        this(status, message, null);
    }
}
