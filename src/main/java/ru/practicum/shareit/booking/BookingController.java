package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> addBooking(@RequestBody @Valid BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Получен запрос POST /bookings");
        return ResponseEntity.status(CREATED).body(bookingService.addBooking(bookingDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long bookingId, @RequestParam(name = "approved") boolean isApproved) {
        log.info("Получен запрос PATCH /bookings/{bookingId}");
        return ResponseEntity.status(OK).body(bookingService.patchBooking(ownerId, bookingId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long requesterId, @PathVariable Long bookingId) {
        log.info("Получен запрос GET /bookings/{bookingId}");
        return ResponseEntity.ok(bookingService.getBookingById(requesterId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllUsersBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state) {
        log.info("Получен запрос GET /bookings?state={state}");
        return ResponseEntity.ok(bookingService.getAllUsersBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllItemOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state) {
        log.info("Получен запрос GET /bookings/owner?state={state}");
        return ResponseEntity.ok(bookingService.getAllItemOwnerBookings(ownerId, state));
    }
}
