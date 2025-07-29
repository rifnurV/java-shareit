package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User findByUserId(Long id);

    User save(User user);

    User update(Long id, User user);

    void delete(Long id);

    List<User> findAll();

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
