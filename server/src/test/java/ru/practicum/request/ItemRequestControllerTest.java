package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    void createItemRequest_ShouldReturnCreatedRequest() throws Exception {
        Long requesterId = 1L;
        String text = "Need a drill";

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description(text)
                .requestorId(requesterId)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        when(requestService.add(requesterId, text)).thenReturn(responseDto);

        mvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(text))
                .andExpect(jsonPath("$.requestorId").value(requesterId));

        verify(requestService, times(1)).add(requesterId, text);
    }

    @Test
    void getByRequestId_ShouldReturnRequest() throws Exception {
        Long requestId = 1L;

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Need a drill")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        when(requestService.get(requestId)).thenReturn(responseDto);


        mvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(requestService, times(1)).get(requestId);
    }

    @Test
    void getByRequestId_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long requestId = 999L;

        when(requestService.get(requestId)).thenThrow(new NotFoundException("Запрос с таким id не найден"));

        mvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Запрос с таким id не найден"));

        verify(requestService, times(1)).get(requestId);
    }

    @Test
    void getAll_ShouldReturnAllRequests() throws Exception {
        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(1L)
                .description("Need drill")
                .requestorId(1L)
                .items(List.of())
                .build();

        ItemRequestDto request2 = ItemRequestDto.builder()
                .id(2L)
                .description("Need hammer")
                .requestorId(2L)
                .items(List.of())
                .build();

        when(requestService.getAll()).thenReturn(List.of(request1, request2));

        mvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Need hammer"));

        verify(requestService, times(1)).getAll();
    }

    @Test
    void getAll_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getAll()).thenReturn(List.of());

        mvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestService, times(1)).getAll();
    }

    @Test
    void getAllByRequesterId_ShouldReturnUserRequests() throws Exception {
        Long requesterId = 1L;

        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(1L)
                .description("Need drill")
                .requestorId(requesterId)
                .items(List.of())
                .build();

        ItemRequestDto request2 = ItemRequestDto.builder()
                .id(2L)
                .description("Need saw")
                .requestorId(requesterId)
                .items(List.of())
                .build();

        when(requestService.getByUserId(requesterId)).thenReturn(List.of(request1, request2));

        mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requestorId").value(requesterId))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].requestorId").value(requesterId));

        verify(requestService, times(1)).getByUserId(requesterId);
    }

     @Test
    void getAllByRequesterId_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        Long requesterId = 1L;

        when(requestService.getByUserId(requesterId)).thenReturn(List.of());

        mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestService, times(1)).getByUserId(requesterId);
    }
}
