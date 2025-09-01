package ru.practicum.request;

import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(Request request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestorId())
                .created(request.getCreated())
                .build();
    }

    public static Request toRequest(ItemRequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestorId(requestDto.getRequestorId());
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public static List<Request> toRequest(List<ItemRequestDto> itemsDto) {
        return itemsDto.stream().map(ItemRequestMapper::toRequest).toList();
    }

    public static List<ItemRequestDto> toDto(List<Request> items) {
        return items.stream().map(ItemRequestMapper::toDto).toList();
    }
}
