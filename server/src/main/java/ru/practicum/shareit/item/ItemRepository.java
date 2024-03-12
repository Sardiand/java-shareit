package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE upper(i.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(i.description) LIKE upper(concat('%', ?1, '%'))" +
            "AND i.available = TRUE ")
    List<Item> findAllByTextRequest(String request, Pageable pageable);

    @Query("SELECT i.id FROM Item i " +
            "WHERE i.owner.id = ?1")
    List<Long> findAllItemIdsByUserId(long userId);

    @Query(nativeQuery = true, name = "ItemBDByID")
    Optional<ItemBookingDto> findItemBookingDtoById(@Param("id") long itemId);

    @Query(nativeQuery = true, name = "ItemBookingDtos")
    List<ItemBookingDto> findAllItemBookingDto(@Param("id") long userId, Pageable pageable);

    @Query(nativeQuery = true, name = "ItemForRequestDtosById")
    List<ItemDto> findAllByRequestId(@Param("id") long requestId);

    @Query(nativeQuery = true, name = "ItemForRequestDtosAll")
    List<ItemDto> findAllWithRequestId(@Param("id") long userId);

    @Query(nativeQuery = true, name = "ItemForRequestDtosByRequester")
    List<ItemDto> findAllByRequesterId(@Param("id") long requesterId);
}
