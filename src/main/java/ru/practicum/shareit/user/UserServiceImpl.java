package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return userMapper.toUserDto(user.get());
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
        if (userRepository.existsByName(name)) {
            throw new NotFoundException("Username is used");
        }

    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {

        Optional<User> userOld = userRepository.findById(userId);
        if (userOld == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new ValidationException("Email is not valid");
            }
            if (!Objects.equals(userOld.get().getEmail(), userDto.getEmail()) &&
                    userRepository.existsByEmail(userDto.getEmail())) {
                throw new ConflictException("Email is already used");
            }
            userOld.get().setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            userOld.get().setName(userDto.getName());
        }

        User updatedUser = userRepository.save(userOld.get());
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        if (userId == null || userId == 0) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
