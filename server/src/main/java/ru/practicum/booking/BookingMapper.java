package ru.practicum.booking;

import org.springframework.stereotype.Component;
import ru.practicum.booking.dto.BookingDto;

import java.util.List;

@Component
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItemId(booking.getItemId());
        bookingDto.setBookerId(booking.getBookerId());
        return bookingDto;
    }

    public static List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    public static Booking toBookingEntity(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId() != null ? bookingDto.getId() : null);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(bookingDto.getBookerId());
        return booking;
    }
}
