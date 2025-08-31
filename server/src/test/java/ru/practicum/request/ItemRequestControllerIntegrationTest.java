package ru.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    void createItemRequest_Integration_ShouldReturnCreated() throws Exception {
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
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(text));

        verify(requestService, times(1)).add(requesterId, text);
    }

    @Test
    void getByRequestId_Integration_WhenNotFound_ShouldHandleException() throws Exception {
        Long requestId = 999L;

        when(requestService.get(requestId)).thenThrow(new NotFoundException("Запрос с таким id не найден"));

        mvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Запрос с таким id не найден"));

        verify(requestService, times(1)).get(requestId);
    }

    @Test
    void getAllByRequesterId_Integration_ShouldReturnUserRequests() throws Exception {
        Long requesterId = 1L;

        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(1L)
                .description("Need drill")
                .requestorId(requesterId)
                .items(List.of())
                .build();

        when(requestService.getByUserId(requesterId)).thenReturn(List.of(request1));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requestorId").value(requesterId));

        verify(requestService, times(1)).getByUserId(requesterId);
    }
}