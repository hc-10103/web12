package edu.handong.week12.common;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(int status, String error, String message, Map<String, String> fields, LocalDateTime timestamp) {
    public static ErrorResponse of(int status, String error, String message, Map<String, String> fields) {
        return new ErrorResponse(status, error, message, fields, LocalDateTime.now());
    }
}
