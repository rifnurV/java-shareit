package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;


    private UserDto userDto;
    private final Long userId = 1L;
    private final Long invalidId = 999L;
    private final NotFoundException notFoundException = new NotFoundException("Exception");

    @BeforeEach
    void setUp() {

        userDto = UserDto.builder()
                .id(null)
                .email("rifnur@yandex.ru")
                .name("Rifnur")
                .build();
    }

    @Test
    @SneakyThrows
    void findById() {
        when(service.getUserByUserId(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(service, times(1)).getUserByUserId(anyLong());
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {
        // Arrange
        Long userId = 999L;
        String expectedErrorMessage = "User with id " + userId + " not found";

        when(service.getUserByUserId(anyLong()))
                .thenThrow(new NotFoundException(expectedErrorMessage));

        // Act & Assert
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    @SneakyThrows
    void create() {
        when(service.create(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(service, times(1)).create(any(UserDto.class));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto updatedUser = UserDto.builder()
                .name("updateduser")
                .email("updated@example.com")
                .build();

        when(service.update(eq(1L), any(UserDto.class))).thenReturn(updatedUser);

        mvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(service).update(eq(1L), any(UserDto.class));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(userId);
    }

}