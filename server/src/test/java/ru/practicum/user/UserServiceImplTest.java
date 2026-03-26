package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void update_WhenEmailAlreadyExists_ShouldThrowConflictException() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("existing@email.com")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> userService.update(userId, userDto));

        assertEquals("Email is already used", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail("existing@email.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WhenEmailSameAsCurrent_ShouldNotThrowException() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("same@email.com")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("same@email.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("same@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(userId, userDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("same@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).existsByEmail(any()); // Не должно проверять существование того же email
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void update_WhenEmailDifferentButNotExists_ShouldUpdateSuccessfully() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("new@email.com")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(userId, userDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("new@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail("new@email.com");
        verify(userRepository, times(1)).save(any());
    }

}