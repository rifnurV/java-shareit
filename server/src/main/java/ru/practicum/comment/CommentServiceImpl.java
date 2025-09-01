package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Autowired
    @Lazy
    private BookingService bookingService;

    @Override
    public List<CommentDto> getAll() {
        List<CommentDto> comments = commentRepository.findAll().stream()
                .map(CommentMapper::toCommentDto)
                .toList();
        return addUserInfo(comments);
    }

    @Override
    public CommentDto get(Long id) {
        CommentDto comment = commentRepository.findById(id)
                .map(CommentMapper::toCommentDto)
                .orElseThrow(() -> new NotFoundException("Комментарий с таким id не найден"));
        return addUserInfo(List.of(comment)).get(0);
    }

    @Override
    @Transactional
    public CommentDto add(CommentDto commentDto) {
        checkItemBookedAndApproved(commentDto.getAuthorId(), commentDto.getItemId());
        return save(commentDto);
    }

    @Override
    @Transactional
    public CommentDto update(CommentDto commentDto) {
        return save(commentDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentDto> findByItemId(Long itemId) {
        List<CommentDto> commentsDto = CommentMapper.toCommentDto(commentRepository.findByItemId(itemId));
        return addUserInfo(commentsDto);
    }

    @Override
    public List<CommentDto> findByItemId(List<Long> itemIds) {
        List<CommentDto> commentsDto = CommentMapper.toCommentDto(commentRepository.findByItemIdIn(itemIds));
        return addUserInfo(commentsDto);
    }

    @Override
    public List<CommentDto> findByAuthorId(Long authorId) {
        List<CommentDto> commentsDto = CommentMapper.toCommentDto(commentRepository.findByAuthorId(authorId));
        return addUserInfo(commentsDto);
    }

    private List<CommentDto> addUserInfo(List<CommentDto> comments) {
        List<Long> authorsIds = comments.stream().map(CommentDto::getAuthorId).toList();
        List<UserDto> users = userService.get(authorsIds);
        Map<Long, String> usersNames = users.stream()
                .collect(Collectors.toMap(
                        UserDto::getId,
                        UserDto::getName));

        return comments.stream()
                .map(x -> {
                    String name = usersNames.getOrDefault(x.getAuthorId(), "no name");
                    x.setAuthorName(name);
                    return x;
                }).toList();
    }

    @Transactional
    private CommentDto addUserInfo(CommentDto comment) {
        return addUserInfo(List.of(comment)).get(0);
    }

    @Transactional
    private CommentDto save(CommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        CommentDto commentSaved = CommentMapper.toCommentDto(commentRepository.save(comment));
        return addUserInfo(List.of(commentSaved)).get(0);
    }

    private void checkItemBookedAndApproved(Long bookerId, Long itemId) {
        List<BookingDto> bookings = bookingService.getByBookerAndItemAndStatus(bookerId, itemId,
                BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ValidationException("Чтобы оставить комментарий, нужно взять вещь в аренду");
        }
        if (!bookings.get(0).getStatus().equals(BookingStatus.PAST)) {
            throw new ValidationException("Чтобы оставить комментарий, нужно дождаться, пока закончится срок аренды");
        }
    }
}
