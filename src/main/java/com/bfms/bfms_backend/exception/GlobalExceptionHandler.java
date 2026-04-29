package com.bfms.bfms_backend.exception;

import com.bfms.bfms_backend.dtos.res.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import com.bfms.bfms_backend.service.AuditService;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    // 1. Xử lý lỗi Bean Validation (dữ liệu đầu vào không hợp lệ)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                errors);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    // 2. Xử lý lỗi nghiệp vụ tùy chỉnh (AppException)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    // 3. Xử lý lỗi xác thực (Sai username/password)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    // 4. Xử lý lỗi phân quyền (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        // Ghi log bảo mật khi bị từ chối truy cập
        auditService.log("ACCESS_DENIED", "Truy cập bị từ chối: " + ex.getMessage());

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    // 5. Xử lý lỗi Runtime chung (Nếu chưa được map sang AppException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeExceptions(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "RUNTIME_ERROR",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 6. Xử lý tất cả các lỗi khác (Lỗi server không xác định)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
