package ru.practicum.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;

import java.util.List;

import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestBody final BookingDto bookingDto,
                                 @RequestHeader(X_SHARER_USER_ID) Long bookerId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.addBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = X_SHARER_USER_ID, required = true) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(defaultValue = "true") boolean approved) {
        log.info("Получен запрос PATCH /bookings/{bookingId}");
        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Long requesterId, @PathVariable Long bookingId) {
        log.info("Получен запрос GET /bookings/{bookingId}");
        return bookingService.getBookingById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUsersBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingStatus status) {
        log.info("Получен запрос GET /bookings?state={state}");
        return bookingService.getAllUsersBookings(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(
            @RequestParam(defaultValue = "ALL") BookingStatus state,
            @RequestHeader(value = X_SHARER_USER_ID, required = false) @Positive Long userId) {
        return bookingService.getByOwner(userId, state);
    }
}
