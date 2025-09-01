package ru.practicum.request;

import org.junit.jupiter.api.Test;
import ru.practicum.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toDto_ShouldMapRequestToDto() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestorId(10L);
        request.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(10L, dto.getRequestorId());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), dto.getCreated());
        assertNull(dto.getItems()); // Items should be null as they are set separately
    }

    @Test
    void toRequest_ShouldMapDtoToRequest() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a hammer")
                .requestorId(20L)
                .created(LocalDateTime.of(2023, 2, 1, 14, 30))
                .build();

        Request request = ItemRequestMapper.toRequest(dto);

        assertNotNull(request);
        assertEquals(1L, request.getId());
        assertEquals("Need a hammer", request.getDescription());
        assertEquals(20L, request.getRequestorId());
        assertEquals(LocalDateTime.of(2023, 2, 1, 14, 30), request.getCreated());
    }

    @Test
    void toRequest_ShouldHandleDtoWithNullFields() {
        ItemRequestDto dto = ItemRequestDto.builder().build();

        Request request = ItemRequestMapper.toRequest(dto);

        assertNotNull(request);
        assertNull(request.getId());
        assertNull(request.getDescription());
        assertNull(request.getRequestorId());
        assertNull(request.getCreated());
    }

    @Test
    void toDtoList_ShouldMapListOfRequestsToDtos() {
        Request request1 = new Request();
        request1.setId(1L);
        request1.setDescription("Request 1");
        request1.setRequestorId(1L);

        Request request2 = new Request();
        request2.setId(2L);
        request2.setDescription("Request 2");
        request2.setRequestorId(2L);

        List<Request> requests = List.of(request1, request2);

        List<ItemRequestDto> dtos = ItemRequestMapper.toDto(requests);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Request 1", dtos.get(0).getDescription());
        assertEquals(1L, dtos.get(0).getRequestorId());

        assertEquals(2L, dtos.get(1).getId());
        assertEquals("Request 2", dtos.get(1).getDescription());
        assertEquals(2L, dtos.get(1).getRequestorId());
    }

    @Test
    void toDtoList_ShouldHandleEmptyList() {
        List<Request> emptyList = List.of();

        List<ItemRequestDto> dtos = ItemRequestMapper.toDto(emptyList);

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void toRequestList_ShouldMapListOfDtosToRequests() {
        ItemRequestDto dto1 = ItemRequestDto.builder()
                .id(1L)
                .description("Dto 1")
                .requestorId(1L)
                .build();

        ItemRequestDto dto2 = ItemRequestDto.builder()
                .id(2L)
                .description("Dto 2")
                .requestorId(2L)
                .build();

        List<ItemRequestDto> dtos = List.of(dto1, dto2);

        List<Request> requests = ItemRequestMapper.toRequest(dtos);

        assertNotNull(requests);
        assertEquals(2, requests.size());

        assertEquals(1L, requests.get(0).getId());
        assertEquals("Dto 1", requests.get(0).getDescription());
        assertEquals(1L, requests.get(0).getRequestorId());

        assertEquals(2L, requests.get(1).getId());
        assertEquals("Dto 2", requests.get(1).getDescription());
        assertEquals(2L, requests.get(1).getRequestorId());
    }

    @Test
    void toRequestList_ShouldHandleEmptyList() {
        List<ItemRequestDto> emptyList = List.of();

        List<Request> requests = ItemRequestMapper.toRequest(emptyList);

        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

     @Test
    void toDto_ShouldIgnoreItemsField() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Test");
        request.setRequestorId(1L);

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertNotNull(dto);
        assertNull(dto.getItems()); // Items should not be mapped by mapper
    }

    @Test
    void toRequest_ShouldIgnoreItemsField() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Test")
                .requestorId(1L)
                .items(List.of()) // Items should be ignored
                .build();

        Request request = ItemRequestMapper.toRequest(dto);

        assertNotNull(request);
    }

    @Test
    void bidirectionalMapping_ShouldBeConsistent() {
        Request originalRequest = new Request();
        originalRequest.setId(1L);
        originalRequest.setDescription("Original description");
        originalRequest.setRequestorId(100L);
        originalRequest.setCreated(LocalDateTime.now());

        ItemRequestDto dto = ItemRequestMapper.toDto(originalRequest);
        Request mappedRequest = ItemRequestMapper.toRequest(dto);

        assertEquals(originalRequest.getId(), mappedRequest.getId());
        assertEquals(originalRequest.getDescription(), mappedRequest.getDescription());
        assertEquals(originalRequest.getRequestorId(), mappedRequest.getRequestorId());
        assertEquals(originalRequest.getCreated(), mappedRequest.getCreated());
    }
}