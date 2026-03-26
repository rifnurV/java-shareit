package ru.practicum.booking;

import ru.practicum.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, Long bookerId);

    BookingDto patchBooking(Long ownerId, Long bookingId, boolean isApproved);

    BookingDto getBookingById(Long requesterId, Long bookingId);

    List<BookingDto> getAllUsersBookings(Long usersId, BookingStatus status);

    List<BookingDto> getByOwner(Long userId, BookingStatus state);

    List<BookingDto> getByBookerAndItemAndStatus(Long userId, Long itemId, BookingStatus state);

}
