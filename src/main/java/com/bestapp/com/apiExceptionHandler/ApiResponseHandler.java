package com.bestapp.com.apiExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiResponseHandler {

    public ResponseEntity<ApiResponse> createSuccessResponse(String message) {
        ApiResponse response = new ApiResponse(message, true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse> createErrorResponse(String message) {
        ApiResponse response = new ApiResponse(message, false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        ApiResponse response = new ApiResponse("An error occurred: " + ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
