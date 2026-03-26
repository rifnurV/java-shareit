package ru.practicum.booking;

import org.springframework.stereotype.Component;
import ru.practicum.booking.dto.BookingDto;

import java.util.List;

@Component
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .build();
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
