package ru.practicum.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(X_SHARER_USER_ID) @Positive Long requesterId,
                                                    @RequestBody final ItemRequestDto itemRequestDto) {

        log.info("START endpoint `method:POST /requests` (create itemRequest), request: {}.", itemRequestDto.getDescription());

        return itemRequestClient.createItemRequest(requesterId, itemRequestDto.getDescription());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @Positive @PathVariable Long requestId) {

        log.info("START endpoint `method:GET /requests/:requestId` (get itemRequest by id), itemRequest id: {}.", requestId);

        return itemRequestClient.getByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {

        log.info("START endpoint `method:GET /requests` (get all itemRequests)");

        return itemRequestClient.getAll();
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequesterId(@RequestHeader(X_SHARER_USER_ID) Long requesterId) {

        log.info("START endpoint `method:GET /requests` (get all itemRequests by requester id), requester id: {}.", requesterId);

        return itemRequestClient.getAllByRequesterId(requesterId);
    }
}
