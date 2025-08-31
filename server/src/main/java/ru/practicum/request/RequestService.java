package ru.practicum.request;

import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(Long userId, String text);

    ItemRequestDto get(Long requestId);

    List<ItemRequestDto> getByUserId(Long userId);

    List<ItemRequestDto> getAll();
}