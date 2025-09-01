package ru.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getByBookerId(Long userId);

    List<Booking> getByBookerIdAndStatus(Long userId, BookingStatus state);

    List<Booking> getByItemIdInAndStatus(List<Long> itemsIds, BookingStatus state);

    List<Booking> getByBookerIdAndItemIdAndStatus(Long userId, Long itemId, BookingStatus state);

    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatusIsNot(Long itemId, BookingStatus status);
}