package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void add_ShouldSaveRequestAndReturnDtoWithItems() {
        Long userId = 1L;
        String text = "Need a drill";

        Request request = new Request();
        request.setId(1L);
        request.setRequestorId(userId);
        request.setDescription(text);
        request.setCreated(LocalDateTime.now());

        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .id(1L)
                .description(text)
                .requestorId(userId)
                .items(List.of())
                .build();

        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(itemService.getByRequestIds(anyList())).thenReturn(List.of());

        ItemRequestDto result = requestService.add(userId, text);

        assertNotNull(result);
        assertEquals(text, result.getDescription());
        assertEquals(userId, result.getRequestorId());
        assertTrue(result.getItems().isEmpty());

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void get_WhenRequestExists_ShouldReturnDtoWithItems() {
        Long requestId = 1L;
        Long userId = 1L;

        Request request = new Request();
        request.setId(requestId);
        request.setRequestorId(userId);
        request.setDescription("Need a drill");
        request.setCreated(LocalDateTime.now());

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(requestId)
                .build();

        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Need a drill")
                .requestorId(userId)
                .items(List.of(itemDto))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemService.getByRequestIds(List.of(requestId))).thenReturn(List.of(itemDto));

        ItemRequestDto result = requestService.get(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().get(0).getName());

        verify(requestRepository, times(1)).findById(requestId);
        verify(itemService, times(1)).getByRequestIds(List.of(requestId));
    }

    @Test
    void get_WhenRequestNotExists_ShouldThrowNotFoundException() {
        Long requestId = 999L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.get(requestId));

        assertEquals("Запрос с таким id не найден", exception.getMessage());
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemService, never()).getByRequestIds(anyList());
    }

    @Test
    void getAll_ShouldReturnAllRequestsWithItems() {
        Request request1 = new Request();
        request1.setId(1L);
        request1.setRequestorId(1L);
        request1.setDescription("Need drill");

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequestorId(2L);
        request2.setDescription("Need hammer");

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .requestId(1L)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Hammer")
                .requestId(2L)
                .build();

        when(requestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<ItemRequestDto> result = requestService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Need drill", result.get(0).getDescription());
        assertEquals("Need hammer", result.get(1).getDescription());

        verify(requestRepository, times(1)).findAll();
    }

    @Test
    void getAll_WhenNoRequests_ShouldReturnEmptyList() {
        when(requestRepository.findAll()).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository, times(1)).findAll();
        verify(itemService, never()).getByRequestIds(anyList());
    }

    @Test
    void getByUserId_ShouldReturnUserRequestsWithItems() {
        Long userId = 1L;

        Request request1 = new Request();
        request1.setId(1L);
        request1.setRequestorId(userId);
        request1.setDescription("Need drill");

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequestorId(userId);
        request2.setDescription("Need hammer");

        when(requestRepository.getByRequestorId(userId)).thenReturn(List.of(request1, request2));

        List<ItemRequestDto> result = requestService.getByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getRequestorId());
        assertEquals(userId, result.get(1).getRequestorId());

        verify(requestRepository, times(1)).getByRequestorId(userId);
    }

    @Test
    void getByUserId_WhenNoUserRequests_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(requestRepository.getByRequestorId(userId)).thenReturn(List.of());

        List<ItemRequestDto> result = requestService.getByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestRepository, times(1)).getByRequestorId(userId);
        verify(itemService, never()).getByRequestIds(anyList());
    }

}