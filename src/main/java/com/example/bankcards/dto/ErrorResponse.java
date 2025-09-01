package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String error;
    private int status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    private Map<String, String> details;
    
    public ErrorResponse(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
