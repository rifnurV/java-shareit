package ru.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);


    @Query("SELECT i FROM Item i WHERE i.available = true AND (i.name ILIKE CONCAT('%', :text, '%') OR i.description ILIKE CONCAT('%', :text, '%'))")
    List<Item> search(@Param("text") String text);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    @Query("SELECT i.available FROM Item i WHERE i.id = :itemId")
    Boolean findAvailableByItemId(@Param("itemId") Long itemId);

    List<Item> getByIdIn(List<Long> itemIds);

    boolean existsItemByIdAndOwnerId(Long itemId, Long ownerId);
}
