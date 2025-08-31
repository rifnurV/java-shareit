package ru.practicum.exception;

public class IncorrectUserException extends RuntimeException {
    public IncorrectUserException(String message) {
        super(message);
    }
}