package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void getByUserId_ShouldReturnUserItems() throws Exception {
        Long userId = 1L;
        ItemDto item1 = ItemDto.builder().id(1L).name("Item 1").build();
        ItemDto item2 = ItemDto.builder().id(2L).name("Item 2").build();

        when(itemService.getByUserId(userId)).thenReturn(List.of(item1, item2));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemService, times(1)).getByUserId(userId);
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        Long itemId = 1L;
        ItemDto item = ItemDto.builder().id(itemId).name("Test Item").build();

        when(itemService.findById(itemId)).thenReturn(item);

        mvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).findById(itemId);
    }

    @Test
    void search_ShouldReturnSearchResults() throws Exception {
        String searchText = "test";
        ItemDto item1 = ItemDto.builder().id(1L).name("Test Item 1").build();
        ItemDto item2 = ItemDto.builder().id(2L).name("Test Item 2").build();

        when(itemService.search(searchText)).thenReturn(List.of(item1, item2));

        mvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"));

        verify(itemService, times(1)).search(searchText);
    }

    @Test
    void search_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.search("")).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService, times(1)).search("");
    }

    @Test
    void create_ShouldCreateItem() throws Exception {
        Long userId = 1L;
        ItemDto inputDto = ItemDto.builder()
                .name("New Item")
                .description("Item description")
                .available(true)
                .build();

        ItemDto outputDto = ItemDto.builder()
                .id(1L)
                .name("New Item")
                .description("Item description")
                .available(true)
                .ownerId(userId)
                .build();

        when(itemService.create(any(ItemDto.class), eq(userId))).thenReturn(outputDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Item"))
                .andExpect(jsonPath("$.ownerId").value(userId));

        verify(itemService, times(1)).create(any(ItemDto.class), eq(userId));
    }

    @Test
    void update_ShouldUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        ItemDto inputDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated description")
                .build();

        ItemDto outputDto = ItemDto.builder()
                .id(itemId)
                .name("Updated Item")
                .description("Updated description")
                .available(true)
                .ownerId(userId)
                .build();

        when(itemService.update(eq(itemId), any(ItemDto.class), eq(userId))).thenReturn(outputDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.ownerId").value(userId));

        verify(itemService, times(1)).update(eq(itemId), any(ItemDto.class), eq(userId));
    }

    @Test
    void addComment_ShouldAddComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto inputDto = CommentDto.builder()
                .text("Great item!")
                .build();

        CommentDto outputDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorId(userId)
                .itemId(itemId)
                .authorName("Test User")
                .build();

        when(itemService.addComment(any(CommentDto.class), eq(itemId), eq(userId))).thenReturn(outputDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorId").value(userId))
                .andExpect(jsonPath("$.itemId").value(itemId));

        verify(itemService, times(1)).addComment(any(CommentDto.class), eq(itemId), eq(userId));
    }
}