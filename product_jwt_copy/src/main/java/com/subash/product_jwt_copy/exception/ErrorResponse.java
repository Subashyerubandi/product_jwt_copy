package com.subash.product_jwt_copy.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String error, String message, String path) {
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    public ErrorResponse(String message, int status, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    
}