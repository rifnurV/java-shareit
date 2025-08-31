package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

@UtilityClass
public class BookingValidate {

    public static void bookingDto(@Valid BookingDto booking) {
        if (booking.getStart() == null) {
            throw new ValidationException("Укажите дату начала бронирования");
        }
        if (booking.getEnd() == null) {
            throw new ValidationException("Укажите дату окончания бронирования");
        }

        if (!isDatesStartEndValid(booking.getStart(), booking.getEnd())) {
            throw new ValidationException("Укажите id вещи для комментирования");
        }
    }

    private static boolean isDatesStartEndValid(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!startDateTime.isBefore(endDateTime)) {
            return false;
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (endDateTime.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }
}
