package ru.practicum.user;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserByUserId(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    boolean checkIdExist(Long id);

    List<UserDto> get(List<Long> id);
}
