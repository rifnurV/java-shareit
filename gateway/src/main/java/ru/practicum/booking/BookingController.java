package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.booking.dto.BookingDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingDto bookingDto,
                                             @RequestHeader(value = "X-Sharer-User-Id", required = true) @Positive Long bookerId) {
        log.info("Получен запрос POST /bookings");
        bookingDto.setBookerId(bookerId);
        BookingValidate.bookingDto(bookingDto);
        return bookingClient.add(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(
            @PathVariable Long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id", required = true) @Positive Long userId,
            @RequestParam(defaultValue = "true") boolean approved) {
        return bookingClient.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long requesterId, @PathVariable @Positive Long bookingId) {
        log.info("Получен запрос GET /bookings/{bookingId}");
        return bookingClient.getBookingById(requesterId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersBookings(@RequestHeader(value = "X-Sharer-User-Id", required = true) @Positive Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingStatus status) {
        log.info("Получен запрос GET /bookings?state={state}");
        return bookingClient.getAllUsersBookings(userId, status);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                          @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingStatus status) {
        log.info("Получен запрос GET /bookings/owner?state={state}");
        return bookingClient.getAllItemOwnerBookings(ownerId, status);
    }
}
