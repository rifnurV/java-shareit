package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.UserRepository;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserRepository userRepository;

    @Test
    void create() {
        UserDto user = UserDto.builder()
                .name("name")
                .email("mail1@mail.ru")
                .build();

        UserDto userSaved = userService.create(user);

        ItemDto item = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .ownerId(userSaved.getId())
                .build();

        ItemDto itemSaved = itemService.create(item, userSaved.getId());
        assertTrue(itemSaved.getId() > 0, "Вещь должна добавиться");
    }

    @Test
    void getByUserId() {
        UserDto user = UserDto.builder()
                .name("name25")
                .email("mail25@mail.ru")
                .build();

        UserDto userSaved = userService.create(user);

        ItemDto item = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .ownerId(userSaved.getId())
                        .build();

        itemService.create(item, userSaved.getId());

        List<ItemDto> itemSaved = itemService.getByUserId(userSaved.getId());
        assertTrue(itemSaved.size() == 1, "Вещь должна добавиться");
    }
}