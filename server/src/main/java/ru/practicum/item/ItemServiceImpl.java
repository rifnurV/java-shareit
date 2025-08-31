package ru.practicum.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.booking.BookingStatus.APPROVED;
import static ru.practicum.booking.BookingStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final RequestRepository requestRepository;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto findById(Long id) {
        ItemDto itemDto = itemRepository.findById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        return addCommentsAndBookings(itemDto);
    }

    @Override
    public List<ItemDto> findById(List<Long> ids) {
        return itemRepository.getByIdIn(ids).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        checkExistsUserById(ownerId);
        Long requestId = itemDto.getRequestId();
        System.out.println(requestId);
        if (Objects.nonNull(requestId)) {
            checkExistsRequestById(requestId);
        }
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User from id= " + ownerId + " not found"));

        itemDto.setOwnerId(ownerId);
        Item item = ItemMapper.toEntity(itemDto);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Optional<Item> itemOld = itemRepository.findById(itemId);
        if (itemOld == null) {
            throw new NotFoundException(String.format("Item with id %s not found", itemId));
        }
        if (!itemOld.get().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Owner id mismatch");
        }
        if (itemDto.getName() != null) {
            itemOld.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemOld.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemOld.get().setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(itemOld.get());
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        List<Item> item = itemRepository.search(text);
        return ItemMapper.toItemDtoList(item);
    }

    @Override
    public List<ItemDto> getByRequestIds(List<Long> requestIds) {
        return itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        return addCommentsAndBookings(ItemMapper.toDto(itemRepository.findByOwnerId(userId)));
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long authorId) {
        UserDto userResponseDto = checkExistsUserById(authorId);
        checkExistsItemById(itemId);

        LocalDateTime now = LocalDateTime.now();

        boolean isBookingConfirmed = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, authorId, APPROVED, now);
        if (!isBookingConfirmed) {
            throw new ValidationException(String.format("The user with with the ID - `%d` did not rent item with the ID - `%d`.", authorId, itemId));
        }

        commentDto.setAuthorId(authorId);
        commentDto.setItemId(itemId);

        CommentDto responseDto = CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto)));
        responseDto.setAuthorName(userResponseDto.getName());

        return responseDto;
    }

    @Override
    public boolean checkIdExist(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    @Transactional
    public boolean isItemAvailable(Long itemId) {
        return itemRepository.findAvailableByItemId(itemId);
    }

    private UserDto checkExistsUserById(Long userId) {

        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("The user with the ID - `%d` was not found.", userId))));
    }

    private void checkExistsRequestById(Long requestId) {

        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("The itemRequest with the ID - `%d` was not found.", requestId));
        }
    }

    private List<ItemDto> addCommentsAndBookings(List<ItemDto> itemsDto) {
        List<Long> itemsIds = itemsDto.stream().map(ItemDto::getId).toList();
        List<CommentDto> comments = commentService.findByItemId(itemsIds);

        Map<Long, List<CommentDto>> itemComments = comments.stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return itemsDto.stream()
                .map(item -> {
                    List<CommentDto> c = itemComments.getOrDefault(item.getId(), List.of());
                    item.setComments(c);
                    return item;
                }).toList();
    }

    private ItemDto addCommentsAndBookings(ItemDto itemDto) {
        return addCommentsAndBookings(List.of(itemDto)).get(0);
    }

    private void checkExistsItemById(Long itemId) {

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("The item with the ID - `%d` was not found.", itemId));
        }
    }
}
