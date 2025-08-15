package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.IncorrectUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl  implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto addBooking(BookingDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User wich id=" + bookerId + " not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item wich id=" + bookingDto.getItemId() + " not found"));
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("The user cannot book their item");
        }
        if (!item.isAvailable()) {
            throw new ValidationException("The item has already been booked");
        }
        isBookingTimeCorrect(bookingDto);
        Booking booking = BookingMapper.toBookingEntity(bookingDto, item, booker);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto patchBooking(Long ownerId, Long bookingId, boolean isApproved) {
        Booking existedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not found"));

        if (!Objects.equals(existedBooking.getItem().getOwner().getId(), ownerId)) {
            throw new IncorrectUserException(ownerId + " cannot change the booking status");
        }

        if (existedBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Booking approved");
        }

        existedBooking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(existedBooking));
    }

    @Override
    public BookingResponseDto getBookingById(Long requesterId, Long bookingId) {
        Booking existedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not foundо"));

        Item item = existedBooking.getItem();

        if (!existedBooking.getBooker().getId().equals(requesterId) &&
                !item.getOwner().getId().equals(requesterId)) {
            throw new NotFoundException("The user is neither the owner nor the tenant");
        }

        return BookingMapper.toBookingResponseDto(existedBooking);
    }

    @Override
    public List<BookingResponseDto> getAllUsersBookings(Long usersId, BookingState state) {
        userRepository.findById(usersId)
                .orElseThrow(() -> new NotFoundException("User with  id=" + usersId + " not found"));

        switch (state) {
            case ALL:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdOrderByStartDesc(usersId));
            case CURRENT:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                                usersId, LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(usersId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(usersId, LocalDateTime.now()));
            case WAITING:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(usersId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(usersId, BookingStatus.REJECTED));
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingResponseDto> getAllItemOwnerBookings(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with  id=" + ownerId + " not found"));

        switch (state) {
            case ALL:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId));
            case CURRENT:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now()));
            case WAITING:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.listToResponseBookingDto(
                        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED));
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void isBookingTimeCorrect(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The end time of the booking is incorrect");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The booking start time is incorrect");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("The start and end times cannot be the same.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("The start time must be earlier than the end time.");
        }
    }
}