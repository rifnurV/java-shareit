package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.comment.Comment;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.Item;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(controllers = BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    private User user;
    private UserDto userDto;
    private UserDto userOutputDTO;

    private Item item;
    private ItemDto itemDto;
    private ItemDto itemOutputDto;

    private Comment comment;
    private CommentDto commentOutputDto;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingDto bookingOutputDto;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long commentId = 1L;
    private final Long bookingId = 1L;
    private final Long itemRequestId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime startTime = now.plusHours(1);
    private final LocalDateTime endTime = now.plusHours(2);

    @BeforeEach
    void setUp() {
        setUpUser();
        setUpItem();
        setUpComment();
        setUpBooking();
        setUpItemRequest();
    }

    void setUpUser() {

        user = new User();
        user.setId(userId);
        user.setEmail("mail26@mail.ru");
        user.setName("Rifnur26");

        userOutputDTO = UserDto.builder()
                .id(userId)
                .email("mail26@mail.ru")
                .name("Rifnur26")
                .build();
    }

    void setUpItem() {

        item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setDescription("Description item");
        item.setAvailable(true);
        item.setRequestId(itemRequestId);

        itemRequestDto = ItemRequestDto.builder()
                .id(itemId)
                .description("Description item")
                .build();
    }

    void setUpComment() {

        comment = new Comment();
        comment.setId(commentId);
        comment.setCreated(startTime);
        comment.setText("Add comment from user1");
        comment.setItemId(itemId);
        comment.setAuthorId(userId);

        commentOutputDto = CommentDto.builder()
                .id(commentId)
                .created(startTime)
                .itemId(itemId)
                .authorId(userId)
                .text("Add comment from user1")
                .build();
    }

    void setUpItemRequest() {

        itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setRequestor(user);
        itemRequest.setDescription("Description item 2");
        itemRequest.setCreated(startTime);

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .requestorId(userId)
                .description("Description item 2")
                .created(startTime)
                .items(List.of())
                .build();
    }

    void setUpBooking() {

        booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(startTime);
        booking.setEnd(endTime);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItemId(itemId);
        booking.setBookerId(userId);

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(startTime)
                .end(endTime)
                .status(BookingStatus.WAITING)
                .itemId(itemId)
                .bookerId(userId)
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateBooking() {
        when(service.addBooking(any(BookingDto.class), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(service, times(1)).addBooking(any(BookingDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void testUpdateBooking() {
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(service.patchBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(X_SHARER_USER_ID, userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(service, times(1)).patchBooking(userId, bookingId, true);
    }

    @Test
    @SneakyThrows
    void testGetBooking() {

        bookingDto.setStatus(BookingStatus.APPROVED);

        when(service.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(service, times(1)).getBookingById(userId, bookingId);
    }
}