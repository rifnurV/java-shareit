package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.ItemMapper;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        checkItemAndUserExist(bookingDto.getItemId(), bookerId);

        UserDto booker = validateUserById(bookerId);
        ItemDto item = validateItemById(bookingDto.getItemId());

        Booking bookingNew = BookingMapper.toBookingEntity(bookingDto);

        bookingNew.setBookerId(bookerId);
        bookingNew.setStatus(WAITING);

        BookingDto outputDto = bookingMapper.toDto(bookingRepository.save(bookingNew));
        outputDto.setBooker(booker);
        outputDto.setItem(item);
        return addBookingInfo(outputDto);
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking existedBooking = checkAccessForStatusChangeAndGetBooking(bookingId, ownerId);

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        existedBooking.setStatus(status);
        BookingDto bookingDto = BookingMapper.toDto(bookingRepository.save(existedBooking));
        return addBookingInfo(bookingDto);
    }

    @Override
    public BookingDto getBookingById(Long requesterId, Long bookingId) {
        BookingDto existedBooking = bookingRepository.findById(bookingId)
                .map(BookingMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Booking with id=" + bookingId + " not foundо"));
        return setStatusPast(addBookingInfo(existedBooking));
    }

    @Override
    public List<BookingDto> getAllUsersBookings(Long userId, BookingStatus status) {
        List<BookingDto> res;

        if (status.equals(BookingStatus.ALL)) {
            res = BookingMapper.toDto(bookingRepository.getByBookerId(userId));
        } else {
            res = BookingMapper.toDto(bookingRepository.getByBookerIdAndStatus(userId, status));
        }
        return addBookingInfo(res);
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingStatus state) {
        if (userId == null || !userService.checkIdExist(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        List<Long> itemsIds = itemService.getByUserId(userId).stream().map(ItemDto::getId).toList();
        return BookingMapper.toDto(bookingRepository.getByItemIdInAndStatus(itemsIds, state));
    }

    private UserDto validateUserById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("The user with the ID - `%d` was not found.", userId))));
    }

    private ItemDto validateItemById(Long itemId) {

        ItemDto shortOutputDto = itemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("The item with the ID - `%d` was not found.", itemId))));

        if (!shortOutputDto.getAvailable()) {
            throw new ValidationException(String.format("The item with the ID - `%d` is not available for rent.", itemId));
        }
        return shortOutputDto;
    }

    @Override
    public List<BookingDto> getByBookerAndItemAndStatus(Long userId, Long itemId, BookingStatus status) {
        List<BookingDto> res = BookingMapper
                .toDto(bookingRepository.getByBookerIdAndItemIdAndStatus(userId, itemId, status));

        return setStatusPast(res);
    }

    private List<BookingDto> setStatusPast(List<BookingDto> bookings) {
        return bookings.stream().map(x -> {
            if (x.getEnd().isBefore(LocalDateTime.now())) {
                x.setStatus(BookingStatus.PAST);
            }
            return x;
        }).toList();
    }

    private BookingDto setStatusPast(BookingDto booking) {
        return setStatusPast(List.of(booking)).get(0);
    }

    private Booking checkAccessForStatusChangeAndGetBooking(Long bookingId, Long userId) {
        Booking bookingSaved = checkBookingAndUserExistReturnBooking(bookingId, userId);
        Long itemOwnerId = itemService.findById(bookingSaved.getItemId()).getOwnerId();

        if (!itemOwnerId.equals(userId)) {
            throw new ValidationException("Только владелец вещи может одобрить заказ");
        }
        return bookingSaved;
    }

    private Booking checkBookingAndUserExistReturnBooking(Long bookingId, Long userId) {
        if (bookingId == null || !checkIdExistImpl(bookingId)) {
            throw new NotFoundException("Бронь с таким id не найдена");
        }

        if (userId == null || !userService.checkIdExist(userId)) {
            throw new ValidationException("Пользователь с таким id не найден");
        }

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с таким id не найдена"));
    }

    public boolean checkIdExistImpl(Long id) {
        return bookingRepository.existsById(id);
    }

    private void checkItemAndUserExist(Long bookingId, Long userId) {
        if (!itemService.checkIdExist(bookingId)) {
            throw new NotFoundException("Вещь с таким id не найдена");
        }
        if (!userService.checkIdExist(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if (!itemService.isItemAvailable(bookingId)) {
            throw new ValidationException("Эту вещь сейчас невозможно забронировать");
        }
    }

    private BookingDto addBookingInfo(BookingDto booking) {
        return addBookingInfo(List.of(booking)).get(0);
    }

    private List<BookingDto> addBookingInfo(List<BookingDto> bookings) {
        List<Long> itemsIds = bookings.stream().map(BookingDto::getItemId).toList();
        List<Long> usersIds = bookings.stream().map(BookingDto::getBookerId).toList();

        List<ItemDto> items = itemService.findById(itemsIds);
        List<UserDto> users = userService.get(usersIds);

        Map<Long, ItemDto> itemsByIds = items.stream()
                .collect(Collectors.toMap(ItemDto::getId, Function.identity()));

        Map<Long, UserDto> usersByIds = users.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return bookings.stream()
                .map(x -> {
                    x.setItem(itemsByIds.getOrDefault(x.getItemId(), null));
                    x.setBooker(usersByIds.getOrDefault(x.getBookerId(), null));
                    return x;
                }).toList();
    }

}