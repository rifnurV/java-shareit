package ru.practicum.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) @Positive Long requesterId,
                                            @RequestBody String text) {
        return requestService.add(requesterId, text);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getByRequestId(@PathVariable Long requestId) {
        return requestService.get(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return requestService.getAll();
    }

    @GetMapping
    public List<ItemRequestDto> getAllByRequesterId(@RequestHeader(value = "X-Sharer-User-Id", required = true) @Positive Long requesterId) {

        return requestService.getByUserId(requesterId);
    }
}
