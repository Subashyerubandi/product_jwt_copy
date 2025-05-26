package com.subash.product_jwt_copy.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse2 {
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse2(String message, int status, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    
}