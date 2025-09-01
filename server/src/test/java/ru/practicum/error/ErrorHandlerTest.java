package ru.practicum.error;

import org.junit.jupiter.api.Test;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.IncorrectUserException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException_ShouldReturnNotFoundStatus() {
        String errorMessage = "Item not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        ErrorResponse response = errorHandler.handleNotFoundExceptionException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        String errorMessage = "Invalid input data";
        ValidationException exception = new ValidationException(errorMessage);

        ErrorResponse response = errorHandler.handleValidationExceptionException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void handleConflictException_ShouldReturnConflictStatus() {
        String errorMessage = "Email already exists";
        ConflictException exception = new ConflictException(errorMessage);

        ErrorResponse response = errorHandler.handleValidationExceptionException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void handleIncorrectUserException_ShouldReturnForbiddenStatus() {
        String errorMessage = "Access denied";
        IncorrectUserException exception = new IncorrectUserException(errorMessage);

        ErrorResponse response = errorHandler.handleIncorrectUserException(exception);

        assertNotNull(response);
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerErrorStatus() {
        String errorMessage = "Unexpected error";
        RuntimeException exception = new RuntimeException(errorMessage);

        ErrorResponse response = errorHandler.handleThrowableException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Произошла непредвиденная ошибка."));
        assertTrue(response.getError().contains(errorMessage));
    }

    @Test
    void handleThrowable_WithNullMessage_ShouldReturnDefaultMessage() {
        RuntimeException exception = new RuntimeException();

        ErrorResponse response = errorHandler.handleThrowableException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Произошла непредвиденная ошибка."));
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerError() {
        NullPointerException exception = new NullPointerException("Null pointer");

        ErrorResponse response = errorHandler.handleThrowableException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Произошла непредвиденная ошибка."));
        assertTrue(response.getError().contains("Null pointer"));
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() {
        String errorMessage = "Test error message";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertEquals(errorMessage, errorResponse.getError());
    }

    @Test
    void errorResponse_EqualsWithNull_ShouldReturnFalse() {
        ErrorResponse response = new ErrorResponse("Error");

        assertNotEquals(null, response);
    }

    @Test
    void errorResponse_EqualsWithDifferentClass_ShouldReturnFalse() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error");

        assertNotEquals("string", response);
    }
}