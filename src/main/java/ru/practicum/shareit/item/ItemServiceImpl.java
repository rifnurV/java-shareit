package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsAndComments;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDtoBookingsAndComments findById(Long ownerId, Long itemId) {
        Item existedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        List<Booking> itemBookings = bookingRepository
                .findAllByItemIdAndStatusNotOrderByStartAsc(existedItem.getId(), BookingStatus.REJECTED);

        List<Booking> bookingsBefore = itemBookings.stream()
                .filter(i -> i.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        List<Booking> bookingsAfter = itemBookings.stream()
                .filter(i -> i.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        List<CommentDto> commentDtoList = CommentMapper.listToCommentDto(
                commentRepository.findAllByItemIdOrderByCreatedAsc(itemId)
        );

        BookingShortDto lastBooking = bookingsBefore.isEmpty() ? null : new BookingShortDto(
                bookingsBefore.get(bookingsBefore.size() - 1).getId(),
                bookingsBefore.get(bookingsBefore.size() - 1).getBooker().getId()
        );

        BookingShortDto nextBooking = bookingsAfter.isEmpty() ? null : new BookingShortDto(
                bookingsAfter.get(0).getId(),
                bookingsAfter.get(0).getBooker().getId()
        );

        ItemDtoBookingsAndComments dto = new ItemDtoBookingsAndComments();
        dto.setId(existedItem.getId());
        dto.setName(existedItem.getName());
        dto.setDescription(existedItem.getDescription());
        dto.setAvailable(existedItem.isAvailable());
        dto.setComments(commentDtoList);

        if (existedItem.getOwner().getId().equals(ownerId)) {
            dto.setLastBooking(lastBooking);
            dto.setNextBooking(nextBooking);
        }

        return dto;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User from id= " + ownerId + " not found"));

        Item item = itemMapper.toEntity(itemDto, owner);

        Item newItem = itemRepository.save(item);
        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Optional<Item> itemOld = itemRepository.findById(itemId);
        if (itemOld == null) {
            throw new NotFoundException(String.format("Item with id %s not found", itemId));
        }
        if (!itemOld.get().getOwner().getId().equals(ownerId)) {
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
    public List<ItemDtoBookingsAndComments> findAllByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream().map(item -> {
                    List<Booking> itemBookings = bookingRepository
                            .findAllByItemIdAndStatusNotOrderByStartAsc(item.getId(), BookingStatus.REJECTED);

                    List<Booking> bookingsBefore = itemBookings.stream()
                            .filter(i -> i.getEnd().isBefore(LocalDateTime.now()))
                            .collect(Collectors.toList());
                    List<Booking> bookingsAfter = itemBookings.stream()
                            .filter(i -> i.getStart().isAfter(LocalDateTime.now()))
                            .collect(Collectors.toList());

                    BookingShortDto lastBooking = bookingsBefore.isEmpty() ? null : new BookingShortDto(
                            bookingsBefore.get(bookingsBefore.size() - 1).getId(),
                            bookingsBefore.get(bookingsBefore.size() - 1).getBooker().getId()
                    );

                    BookingShortDto nextBooking = bookingsAfter.isEmpty() ? null : new BookingShortDto(
                            bookingsAfter.get(0).getId(),
                            bookingsAfter.get(0).getBooker().getId()
                    );

                    List<CommentDto> commentDtoList = CommentMapper.listToCommentDto(
                            commentRepository.findAllByItemIdOrderByCreatedAsc(item.getId())
                    );

                    ItemDtoBookingsAndComments dto = new ItemDtoBookingsAndComments();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setDescription(item.getDescription());
                    dto.setAvailable(item.isAvailable());
                    dto.setLastBooking(lastBooking);
                    dto.setNextBooking(nextBooking);
                    dto.setComments(commentDtoList);

                    return dto;
                }).sorted(Comparator.comparing(ItemDtoBookingsAndComments::getId))
                .collect(Collectors.toList());
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
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long authorId) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("The text in the comment is empty");
        }
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + authorId + " not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));

        List<Booking> bookingList = bookingRepository
                .findAllByItemIdAndBookerIdAndEndBefore(itemId, authorId, LocalDateTime.now());

        if (bookingList.isEmpty()) {
            throw new ValidationException("Not rented");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
