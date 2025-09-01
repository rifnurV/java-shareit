package ru.practicum.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.BookingService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getAll_ShouldReturnCommentsWithUserInfo() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthorId(1L);
        comment1.setText("Great item!");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthorId(2L);
        comment2.setText("Good quality");

        UserDto user1 = UserDto.builder().id(1L).name("User One").build();
        UserDto user2 = UserDto.builder().id(2L).name("User Two").build();

        when(commentRepository.findAll()).thenReturn(List.of(comment1, comment2));
        when(userService.get(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<CommentDto> result = commentService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Great item!", result.get(0).getText());
        assertEquals("User One", result.get(0).getAuthorName());
        assertEquals("Good quality", result.get(1).getText());
        assertEquals("User Two", result.get(1).getAuthorName());

        verify(commentRepository, times(1)).findAll();
        verify(userService, times(1)).get(List.of(1L, 2L));
    }

    @Test
    void get_WhenCommentExists_ShouldReturnCommentWithUserInfo() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthorId(1L);
        comment.setText("Test comment");

        UserDto user = UserDto.builder().id(1L).name("Test User").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userService.get(List.of(1L))).thenReturn(List.of(user));

        CommentDto result = commentService.get(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals("Test User", result.getAuthorName());

        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, times(1)).get(List.of(1L));
    }

    @Test
    void get_WhenCommentNotExists_ShouldThrowNotFoundException() {
        Long commentId = 999L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.get(commentId));

        assertEquals("Комментарий с таким id не найден", exception.getMessage());
        verify(commentRepository, times(1)).findById(commentId);
        verify(userService, never()).get(anyList());
    }

    @Test
    void update_ShouldSaveCommentWithoutValidation() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .itemId(1L)
                .text("Updated comment")
                .build();

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setId(1L);

        UserDto user = UserDto.builder().id(1L).name("Test User").build();

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userService.get(List.of(1L))).thenReturn(List.of(user));

        CommentDto result = commentService.update(commentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated comment", result.getText());
        assertEquals("Test User", result.getAuthorName());

        verify(bookingService, never()).getByBookerAndItemAndStatus(anyLong(), anyLong(), any());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(userService, times(1)).get(List.of(1L));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        Long commentId = 1L;
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.delete(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void findByItemId_ShouldReturnCommentsForItem() {
        Long itemId = 1L;

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthorId(1L);
        comment1.setItemId(itemId);
        comment1.setText("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthorId(2L);
        comment2.setItemId(itemId);
        comment2.setText("Comment 2");

        UserDto user1 = UserDto.builder().id(1L).name("User One").build();
        UserDto user2 = UserDto.builder().id(2L).name("User Two").build();

        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment1, comment2));
        when(userService.get(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<CommentDto> result = commentService.findByItemId(itemId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemId, result.get(0).getItemId());
        assertEquals(itemId, result.get(1).getItemId());
        assertEquals("User One", result.get(0).getAuthorName());
        assertEquals("User Two", result.get(1).getAuthorName());

        verify(commentRepository, times(1)).findByItemId(itemId);
        verify(userService, times(1)).get(List.of(1L, 2L));
    }

    @Test
    void findByItemId_WithMultipleItems_ShouldReturnComments() {
        List<Long> itemIds = List.of(1L, 2L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthorId(1L);
        comment1.setItemId(1L);
        comment1.setText("Comment 1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthorId(2L);
        comment2.setItemId(2L);
        comment2.setText("Comment 2");

        UserDto user1 = UserDto.builder().id(1L).name("User One").build();
        UserDto user2 = UserDto.builder().id(2L).name("User Two").build();

        when(commentRepository.findByItemIdIn(itemIds)).thenReturn(List.of(comment1, comment2));
        when(userService.get(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<CommentDto> result = commentService.findByItemId(itemIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getItemId());
        assertEquals(2L, result.get(1).getItemId());

        verify(commentRepository, times(1)).findByItemIdIn(itemIds);
        verify(userService, times(1)).get(List.of(1L, 2L));
    }

    @Test
    void getAll_WhenNoComments_ShouldReturnEmptyList() {
        when(commentRepository.findAll()).thenReturn(List.of());

        List<CommentDto> result = commentService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void findByItemId_WhenNoComments_ShouldReturnEmptyList() {
        Long itemId = 1L;
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of());

        List<CommentDto> result = commentService.findByItemId(itemId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findByItemId(itemId);
    }

    @Test
    void findByAuthorId_WhenNoComments_ShouldReturnEmptyList() {
        Long authorId = 1L;
        when(commentRepository.findByAuthorId(authorId)).thenReturn(List.of());

        List<CommentDto> result = commentService.findByAuthorId(authorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findByAuthorId(authorId);
    }
}
