package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable Long userId) {
        UserDto user = userService.getUserByUserId(userId);
        if (user == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(CREATED).body(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") Long userId,
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("userId") Long userId) {
        userService.delete(userId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
