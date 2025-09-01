package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.booking.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ru.practicum.user.UserMapper userMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addBooking_WhenItemNotAvailable_ShouldThrowValidationException() {
        Long bookerId = 1L;
        Long itemId = 1L;

        BookingDto inputDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        when(userService.checkIdExist(bookerId)).thenReturn(true);
        when(itemService.checkIdExist(itemId)).thenReturn(true);
        when(itemService.isItemAvailable(itemId)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(inputDto, bookerId));

        assertEquals("Эту вещь сейчас невозможно забронировать", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void patchBooking_WhenApproved_ShouldUpdateStatus() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Long itemId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItemId(itemId);
        booking.setStatus(WAITING);

        ItemDto itemDto = ItemDto.builder().id(itemId).ownerId(ownerId).build();
        BookingDto bookingDto = BookingDto.builder().id(bookingId).status(APPROVED).build();

        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userService.checkIdExist(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.findById(itemId)).thenReturn(itemDto);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.patchBooking(ownerId, bookingId, true);

        assertNotNull(result);
        assertEquals(APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void patchBooking_WhenRejected_ShouldUpdateStatus() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Long itemId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItemId(itemId);
        booking.setStatus(WAITING);

        ItemDto itemDto = ItemDto.builder().id(itemId).ownerId(ownerId).build();
        BookingDto bookingDto = BookingDto.builder().id(bookingId).status(REJECTED).build();

        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userService.checkIdExist(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.findById(itemId)).thenReturn(itemDto);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.patchBooking(ownerId, bookingId, false);

        assertNotNull(result);
        assertEquals(REJECTED, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void patchBooking_WhenNotOwner_ShouldThrowValidationException() {
        Long ownerId = 1L;
        Long otherUserId = 2L;
        Long bookingId = 1L;
        Long itemId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItemId(itemId);

        ItemDto itemDto = ItemDto.builder().id(itemId).ownerId(otherUserId).build();

        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userService.checkIdExist(ownerId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.findById(itemId)).thenReturn(itemDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, true));

        assertEquals("Только владелец вещи может одобрить заказ", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        Long requesterId = 1L;
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookerId(requesterId);
        booking.setEnd(LocalDateTime.now().minusHours(1)); // Past booking

        BookingDto bookingDto = BookingDto.builder()
                .id(bookingId)
                .bookerId(requesterId)
                .end(LocalDateTime.now().minusHours(1))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(requesterId, bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_WhenNotFound_ShouldThrowNotFoundException() {

        Long bookingId = 999L;
        Long requesterId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(requesterId, bookingId));

        assertEquals("Booking with id=999 not foundо", exception.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllUsersBookings_WithAllStatus_ShouldReturnAllBookings() {
        Long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBookerId(userId);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBookerId(userId);

        BookingDto dto1 = BookingDto.builder().id(1L).bookerId(userId).build();
        BookingDto dto2 = BookingDto.builder().id(2L).bookerId(userId).build();

        when(bookingRepository.getByBookerId(userId)).thenReturn(List.of(booking1, booking2));

        List<BookingDto> result = bookingService.getAllUsersBookings(userId, ALL);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).getByBookerId(userId);
        verify(bookingRepository, never()).getByBookerIdAndStatus(any(), any());
    }

    @Test
    void getAllUsersBookings_WithSpecificStatus_ShouldReturnFilteredBookings() {
        Long userId = 1L;

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookerId(userId);
        booking.setStatus(WAITING);

        BookingDto dto = BookingDto.builder().id(1L).bookerId(userId).status(WAITING).build();

        when(bookingRepository.getByBookerIdAndStatus(userId, WAITING)).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllUsersBookings(userId, WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(WAITING, result.get(0).getStatus());
        verify(bookingRepository, times(1)).getByBookerIdAndStatus(userId, WAITING);
        verify(bookingRepository, never()).getByBookerId(any());
    }

    @Test
    void getByOwner_ShouldReturnOwnerBookings() {
        Long ownerId = 1L;
        List<Long> itemIds = List.of(1L, 2L);

        ItemDto item1 = ItemDto.builder().id(1L).ownerId(ownerId).build();
        ItemDto item2 = ItemDto.builder().id(2L).ownerId(ownerId).build();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItemId(1L);

        BookingDto bookingDto = BookingDto.builder().id(1L).itemId(1L).build();

        when(userService.checkIdExist(ownerId)).thenReturn(true);
        when(itemService.getByUserId(ownerId)).thenReturn(List.of(item1, item2));
        when(bookingRepository.getByItemIdInAndStatus(eq(itemIds), any())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getByOwner(ownerId, ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemService, times(1)).getByUserId(ownerId);
        verify(bookingRepository, times(1)).getByItemIdInAndStatus(eq(itemIds), any());
    }

    @Test
    void getByOwner_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long ownerId = 999L;

        when(userService.checkIdExist(ownerId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getByOwner(ownerId, ALL));

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
        verify(itemService, never()).getByUserId(any());
    }

    @Test
    void getByBookerAndItemAndStatus_ShouldReturnFilteredBookings() {
        Long userId = 1L;
        Long itemId = 1L;

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookerId(userId);
        booking.setItemId(itemId);
        booking.setStatus(APPROVED);
        booking.setEnd(LocalDateTime.now().minusHours(1)); // Past booking

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .bookerId(userId)
                .itemId(itemId)
                .status(APPROVED)
                .end(LocalDateTime.now().minusHours(1))
                .build();

        when(bookingRepository.getByBookerIdAndItemIdAndStatus(userId, itemId, APPROVED))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getByBookerAndItemAndStatus(userId, itemId, APPROVED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PAST, result.get(0).getStatus()); // Should be updated to PAST
        verify(bookingRepository, times(1)).getByBookerIdAndItemIdAndStatus(userId, itemId, APPROVED);
    }

    @Test
    void checkIdExistImpl_ShouldReturnTrueWhenExists() {
        Long bookingId = 1L;
        when(bookingRepository.existsById(bookingId)).thenReturn(true);

        boolean result = bookingService.checkIdExistImpl(bookingId);

        assertTrue(result);
        verify(bookingRepository, times(1)).existsById(bookingId);
    }

    @Test
    void checkIdExistImpl_ShouldReturnFalseWhenNotExists() {
        Long bookingId = 999L;
        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        boolean result = bookingService.checkIdExistImpl(bookingId);

        assertFalse(result);
        verify(bookingRepository, times(1)).existsById(bookingId);
    }

    @Test
    void addBooking_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long bookerId = 999L;
        Long itemId = 1L;

        BookingDto inputDto = BookingDto.builder().itemId(itemId).build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(inputDto, bookerId));

        assertEquals("Вещь с таким id не найдена", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_WhenItemNotFound_ShouldThrowNotFoundException() {
        Long bookerId = 1L;
        Long itemId = 999L;

        BookingDto inputDto = BookingDto.builder().itemId(itemId).build();

        when(itemService.checkIdExist(itemId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(inputDto, bookerId));

        assertEquals("Вещь с таким id не найдена", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void patchBooking_WhenBookingNotFound_ShouldThrowNotFoundException() {
        Long ownerId = 1L;
        Long bookingId = 999L;

        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, true));

        assertEquals("Бронь с таким id не найдена", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void patchBooking_WhenUserNotFound_ShouldThrowValidationException() {
        Long ownerId = 999L;
        Long bookingId = 1L;

        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        when(userService.checkIdExist(ownerId)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, true));

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getAllUsersBookings_WhenNoBookings_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(bookingRepository.getByBookerId(userId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.getAllUsersBookings(userId, ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).getByBookerId(userId);
    }

    @Test
    void getByOwner_WhenNoItems_ShouldReturnEmptyList() {
        Long ownerId = 1L;

        when(userService.checkIdExist(ownerId)).thenReturn(true);
        when(itemService.getByUserId(ownerId)).thenReturn(List.of());
        when(bookingRepository.getByItemIdInAndStatus(any(), any())).thenReturn(List.of());

        List<BookingDto> result = bookingService.getByOwner(ownerId, ALL);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemService, times(1)).getByUserId(ownerId);
    }


}