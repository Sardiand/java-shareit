package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', ?1, '%'))" +
            "AND i.available = TRUE ")
    List<Item> findAllByTextRequest(String request);

    List<Item> findAllByOwner_id(long userId);

    @Query("SELECT i.id FROM Item i " +
            "WHERE i.owner.id = ?1")
    List<Long> findAllItemIdsByUserId(long userId);

    @Query(nativeQuery = true, name = "ItemBDByID")
    Optional<ItemBookingDto> findItemBookingDtoById(long itemId);

    @Query(nativeQuery = true, name = "ItemBookingDtos")
    List<ItemBookingDto> findAllItemBookingDto(long userId);
}
