package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemIdIn(List<Long> itemIds);

    List<Comment> findByItemId(Long itemId);

    List<Comment> findByAuthorId(Long autorId);

    List<Comment> findAllByItemIdOrderByCreatedDesc(Long itemId);

}