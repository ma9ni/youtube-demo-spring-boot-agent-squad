package dev.ahmed.demo.orders.order;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", request, fields);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ResponseEntity<ApiError> missingHeader(MissingRequestHeaderException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Missing required header: " + exception.getHeaderName(), request, Map.of());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ApiError> notFound(OrderNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Order not found", request, Map.of());
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    ResponseEntity<ApiError> conflict(IdempotencyConflictException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Idempotency key was already used with another payload", request, Map.of());
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request,
                                           Map<String, String> fields) {
        return ResponseEntity.status(status).body(
                new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fields));
    }

    record ApiError(Instant timestamp, int status, String error, String message, String path,
                    Map<String, String> fields) {
    }
}

class OrderNotFoundException extends RuntimeException {
}

class IdempotencyConflictException extends RuntimeException {
}

