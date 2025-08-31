package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
