package edu.handong.week12.common;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(String code, String message, int status, Map<String, String> fields, LocalDateTime timestamp) {
    public static ErrorResponse of(String code, String message, int status, Map<String, String> fields) {
        return new ErrorResponse(code, message, status, fields, LocalDateTime.now());
    }
}
