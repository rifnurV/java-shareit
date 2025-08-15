package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);


    @Query("SELECT i FROM Item i WHERE i.available = true AND (i.name ILIKE CONCAT('%', :text, '%') OR i.description ILIKE CONCAT('%', :text, '%'))")
    List<Item> search(@Param("text") String text);
}
