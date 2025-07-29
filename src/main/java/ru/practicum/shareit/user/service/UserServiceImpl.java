package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserByUserId(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateEmailName(userDto.getEmail(), userDto.getName());

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!userDto.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new ValidationException("Email is not valid");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            userDto.setName(userDto.getEmail());
        }
        User newUser = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toUserDto(newUser);
    }

    private void validateEmailName(String email, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email is already used");
        }
        if (userRepository.existsByUsername(name)) {
            throw new NotFoundException("Username is used");
        }

    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {

        User userOld = userRepository.findByUserId(userId);
        if (userOld == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new ValidationException("Email is not valid");
            }
            if (!Objects.equals(userOld.getEmail(), userDto.getEmail()) &&
                    userRepository.existsByEmail(userDto.getEmail())) {
                throw new ConflictException("Email is already used");
            }
            userOld.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            userOld.setName(userDto.getName());
        }

        User updatedUser = userRepository.update(userId, userOld);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        if (userId == null || userId == 0) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }
}
