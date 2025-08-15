package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MapUserRepository implements UserRepository {
    Map<Long, User> users = new HashMap<Long, User>();
    AtomicLong userId = new AtomicLong();

    @Override
    public User findByUserId(Long id) {
        return users.get(id);
    }

    @Override
    public User save(User user) {
        Long id = userId.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User oldUser;
        if (users.containsKey(userId)) {
            oldUser = users.get(userId);
            users.put(userId, user);
        } else {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return oldUser;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getName().equals(username));
    }
}
