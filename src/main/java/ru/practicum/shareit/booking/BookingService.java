package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(BookingDto bookingDto, Long bookerId);

    BookingResponseDto patchBooking(Long ownerId, Long bookingId, boolean isApproved);

    BookingResponseDto getBookingById(Long requesterId, Long bookingId);

    List<BookingResponseDto> getAllUsersBookings(Long usersId, BookingState state);

    List<BookingResponseDto> getAllItemOwnerBookings(Long ownerId, BookingState state);

}
