package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.item.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    private Item item1;
    private Item item2;
    private Item item3;
    private ItemDto itemDto1;
    private ItemDto itemDto2;


    private Booking booking;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;

    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long itemId1 = 1L;
    private final Long itemId2 = 2L;
    private final Long itemId3 = 3L;
    private final Long bookingId1 = 1L;
    private final Long invalidId = 999L;
    private final int from = 0;
    private final int size = 2;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime startTime = now.plusHours(1);
    private final LocalDateTime endTime = now.plusHours(2);

    public void init() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        user1 = new User();
        user1.setEmail("test25@test.com");
        user1.setName("test25");

        userDto1 = UserDto.builder()
                .email("test25@test.com")
                .name("test25")
                .build();

        user2 = new User();
        user2.setEmail("test26@test.com");
        user2.setName("test26");

        userDto2 = UserDto.builder()
                .email("test26@test.com")
                .name("test26")
                .build();

        item1 = new Item();
        item1.setName("item1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwnerId(userId1);

        itemDto1 = new ItemDto();
        itemDto1.setId(itemId1);
        itemDto1.setName("item1");
        itemDto1.setDescription("Description 1");
        itemDto1.setAvailable(true);

        item2 = new Item();
        item2.setName("item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwnerId(userId1);

        itemDto2 = new ItemDto();
        itemDto2.setId(itemId2);
        itemDto2.setName("item 2");
        itemDto2.setDescription("Description  2");
        itemDto2.setAvailable(true);

        item3 = new Item();
        item3.setName("item 3");
        item3.setDescription("Description 3");
        item3.setAvailable(true);
        item3.setOwnerId(userId1);

        booking = new Booking();
        booking.setStart(startTime);
        booking.setEnd(endTime);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBookerId(userId1);
        booking.setItemId(itemId1);

        bookingDto = new BookingDto();
        bookingDto.setStart(startTime);
        bookingDto.setEnd(endTime);
        bookingDto.setItemId(itemId2);

        bookingDto1 = new BookingDto();
        bookingDto.setId(bookingId1);
        bookingDto.setStart(startTime);
        bookingDto.setEnd(endTime);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBookerId(userId1);
        bookingDto.setItemId(itemId2);
    }

    @Test
    @SneakyThrows
    public void testUpdateBookingWithInvalidBookingIdStatusNotFound() {
        mvc.perform(patch("/bookings/{bookingId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @SneakyThrows
    public void testGetAllBookingsWithUserBookerAndStateStatusOk() {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "CURRENT")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }

    @Test
    @SneakyThrows
    public void testGetAllBookingsWithUserBookerAndStatePastStatusOk() {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }

    @Test
    @SneakyThrows
    public void testGetAllBookingsWithUserBookerAndStateRejectedStatusOk() {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId1)
                        .param("state", "REJECTED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }

    @Test
    @SneakyThrows
    public void testGetBookingByIdWithInvalidBookingIdStatusNotFound() {
        mvc.perform(get("/bookings/{bookingId}", invalidId)
                        .header("X-Sharer-User-Id", userId1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @SneakyThrows
    public void testGetAllBookingsWithUserItemOwnerInvalidIdStatusNotFound() {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", invalidId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));

    }
}
