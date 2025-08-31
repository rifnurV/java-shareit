package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.booking.BookingService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingService bookingService;

    private ItemDto itemDto;
    private ItemDto itemDtoCreated;
    private ItemDto itemDto2;
    private ItemDto itemDto3;
    private User user1;
    private User user2;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Long itemId1 = 1L;
    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long invalidId = 999L;
    private final int from = 0;
    private final int size = 2;

    public void init() {
        user1 = new User();
        user1.setEmail("rifnur45@yandex.ru");
        user1.setName("RuRu");
        user2 = new User();
        user2.setEmail("Vakhitov@gmail.com");
        user2.setName("Vakhitov");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @BeforeEach
    public void setUp() {

        itemDto = ItemDto.builder()
                .name("Item")
                .description("Description item")
                .available(true)
                .build();

        itemDtoCreated = ItemDto.builder()
                .id(itemId1)
                .name("Item")
                .description("Description item")
                .available(true)
                .comments(new ArrayList<>())
                .build();
    }

    @Test
    @SneakyThrows
    public void testCreateItemStatusCreated() {
        init();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDtoCreated.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoCreated.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoCreated.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoCreated.getAvailable()));
    }

    @Test
    @SneakyThrows
    public void testCreateItemWithInvalidUserIdStatusNotFound() {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", invalidId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @SneakyThrows
    public void testSearchAllItems_WithText_ReturnsStatusOk() {
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("Item 3")
                .description("Description item 3")
                .available(true)
                .build();
        String text = "tem";

        List<ItemDto> items = Collections.singletonList(itemDto);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testAddComment_WithNotConfirmBooking_ReturnsStatusBadRequest() {
        CommentDto commentDto = CommentDto.builder()
                .text("Add comment from user1").build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @SneakyThrows
    public void testAddComment_WithInvalidItemId_ReturnsStatusNotFound() {
        CommentDto commentDto = CommentDto.builder().text("Add comment from user1").build();


        mvc.perform(post("/items/{itemId}/comment", invalidId)
                        .header("X-Sharer-User-Id", userId2)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        NotFoundException.class));
    }

    @Test
    @SneakyThrows
    public void testSearchAllItems_WithEmptyText_ReturnsStatusOk() {
        String text = "";

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId2)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of())));
    }
}