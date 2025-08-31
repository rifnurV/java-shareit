package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ShareItServer;
import ru.practicum.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
class UserServiceTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;

    @Test
    void getUserByUserId() {

        UserDto user = UserDto.builder()
                .name("nameById")
                .email("nameById@gmail.com")
                .build();

        UserDto userSaved = userController.create(user);
        assertNotNull(userSaved);
        assertEquals(userSaved.getName(), user.getName());
        assertEquals(userSaved.getEmail(), user.getEmail());
    }

    @Test
    void create() {
        UserDto user = UserDto.builder()
                .name("name")
                .email("rifnur4@gmail.com")
                .build();

        UserDto userSaved = userController.create(user);
        assertTrue(userSaved.getId() > 0, "Пользователь должен добавиться");
    }

    @Test
    void update() {
        UserDto user1 = UserDto.builder().name("nameOld").email("nameOld@gmail.com").build();
        UserDto userCreated = userController.create(user1);

        UserDto user2 = UserDto.builder().name("updateRifnur1").email("newrifnur1@gmail.com").build();

        UserDto result = userService.update(userCreated.getId(), user2);

        assertNotNull(result);
        assertEquals("updateRifnur1", result.getName());
        assertEquals("newrifnur1@gmail.com", result.getEmail());

    }

    @Test
    void delete() {
        UserDto user = UserDto.builder()
                .name("name3")
                .email("rifnur3@gmail.com")
                .build();

        UserDto userSaved = userController.create(user);
        assertTrue(userSaved.getId() > 0, "Пользователь должен добавиться");

        userController.deleteUser(userSaved.getId());
        assertThrows(Exception.class, () -> userController.findById(userSaved.getId()),
                "Пользователь должен удалиться");
    }
    @Test
    void addBadEmail() {
        UserDto user = UserDto.builder()
                .name("name10")
                .email("rifnur10gmail.com")
                .build();

        assertThrows(Exception.class, () -> userController.create(user),
                "Пользователь НЕ должен добавиться");
    }

    @Test
    void checkIdExistWhenIdExistsShouldReturnTrue() {
        UserDto user = UserDto.builder()
                .name("name15")
                .email("rifnur15@gmail.com")
                .build();

        userController.create(user);

        boolean result = userService.checkIdExist(1L);
        assertTrue(result);
    }
}