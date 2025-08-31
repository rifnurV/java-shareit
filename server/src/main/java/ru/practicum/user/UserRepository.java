package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByName(String name);

    List<User> getByIdIn(List<Long> userIds);
}
