package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    void get() {
    }

    @Test
    void findById() {
    }

    @Test
    void testFindById() {
    }

    @Test
    void create() {
        UserDto user = UserDto.builder()
                .name("name")
                .email("mail1@mail.ru")
                .build();

        UserDto userSaved = userService.create(user);

        ItemDto item = new ItemDto();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwnerId(userSaved.getId());

        ItemDto itemSaved = itemService.create(item, userSaved.getId());
        assertTrue(itemSaved.getId() > 0, "Вещь должна добавиться");
    }

    @Test
    void getByRequestIds() {
    }

    @Test
    void getByUserId() {
        UserDto user = UserDto.builder()
                .name("name25")
                .email("mail25@mail.ru")
                .build();

        UserDto userSaved = userService.create(user);

        ItemDto item = new ItemDto();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwnerId(userSaved.getId());

        itemService.create(item, userSaved.getId());

        List<ItemDto> itemSaved = itemService.getByUserId(userSaved.getId());
        assertTrue(itemSaved.size() == 1, "Вещь должна добавиться");
    }

    @Test
    void addComment() {
    }

    @Test
    void checkIdExist() {
    }

    @Test
    void isItemAvailable() {
    }
}