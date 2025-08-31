package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto add(Long userId, String text) {
        Request request = new Request();
        request.setRequestorId(userId);
        request.setDescription(text);
        return addItems(ItemRequestMapper.toDto(requestRepository.save(request)));
    }

    @Override
    public ItemRequestDto get(Long requestId) {
        return requestRepository.findById(requestId)
                .map(ItemRequestMapper::toDto)
                .map(this::addItems)
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return requestRepository.findAll().stream()
                .map(ItemRequestMapper::toDto)
                .map(this::addItems)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getByUserId(Long userId) {
        return requestRepository.getByRequestorId(userId).stream()
                .map(ItemRequestMapper::toDto)
                .map(this::addItems)
                .toList();
    }

    private List<ItemRequestDto> addItems(List<ItemRequestDto> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequestDto::getId).toList();
        List<ItemDto> items = itemService.getByRequestIds(requestIds);

        Map<Long, List<ItemDto>> itemByRequestId = items.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> it = itemByRequestId.getOrDefault(request.getId(), List.of());
                    request.setItems(it);
                    return request;
                }).toList();
    }

    private ItemRequestDto addItems(ItemRequestDto requestDto) {
        return addItems(List.of(requestDto)).get(0);
    }


}
