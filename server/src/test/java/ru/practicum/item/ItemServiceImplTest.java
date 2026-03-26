package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void findById_WhenItemNotExists_ShouldThrowNotFoundException() {
        Long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(itemId));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void findById_WithMultipleIds_ShouldReturnItems() {
        List<Long> ids = List.of(1L, 2L);
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);

        ItemDto itemDto1 = ItemDto.builder().id(1L).build();
        ItemDto itemDto2 = ItemDto.builder().id(2L).build();

        when(itemRepository.getByIdIn(ids)).thenReturn(List.of(item1, item2));
        when(itemMapper.toItemDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toItemDto(item2)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.findById(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).getByIdIn(ids);
    }

    @Test
    void update_ShouldReturnItemDto() {
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder().id(itemId).build();
        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.update(itemId, itemDto, 1L);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void search_WithBlankText_ShouldReturnEmptyList() {
        List<ItemDto> result1 = itemService.search("");
        List<ItemDto> result2 = itemService.search("   ");
        List<ItemDto> result3 = itemService.search(null);

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
    }

    @Test
    void search_WithValidText_ShouldReturnResults() {
        String searchText = "test";
        Item item = new Item();
        item.setId(1L);
        ItemDto itemDto = ItemDto.builder().id(1L).build();

        when(itemRepository.search(searchText)).thenReturn(List.of(item));
        when(itemMapper.toItemDtoList(List.of(item))).thenReturn(List.of(itemDto));

        List<ItemDto> result = itemService.search(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).search(searchText);
    }

    @Test
    void getByRequestIds_ShouldReturnItems() {
        List<Long> requestIds = List.of(1L, 2L);
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);

        ItemDto itemDto1 = ItemDto.builder().id(1L).build();
        ItemDto itemDto2 = ItemDto.builder().id(2L).build();

        when(itemRepository.findByRequestIdIn(requestIds)).thenReturn(List.of(item1, item2));
        when(itemMapper.toItemDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toItemDto(item2)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.getByRequestIds(requestIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findByRequestIdIn(requestIds);
    }

    @Test
    void checkIdExist_ShouldReturnTrueWhenExists() {
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        boolean result = itemService.checkIdExist(itemId);

        assertTrue(result);
        verify(itemRepository, times(1)).existsById(itemId);
    }

    @Test
    void isItemAvailable_ShouldReturnAvailability() {
        Long itemId = 1L;
        when(itemRepository.findAvailableByItemId(itemId)).thenReturn(true);

        boolean result = itemService.isItemAvailable(itemId);

        assertTrue(result);
        verify(itemRepository, times(1)).findAvailableByItemId(itemId);
    }
}