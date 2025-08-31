package ru.practicum.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;

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
        return userMapper.toUserDto(userRepository.findById(userId).get());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User newUser = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toUserDto(newUser);
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
    public boolean checkIdExist(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public List<UserDto> get(List<Long> ids) {
        return userRepository.getByIdIn(ids).stream().map(UserMapper::toUserDto).toList();
    }
}
