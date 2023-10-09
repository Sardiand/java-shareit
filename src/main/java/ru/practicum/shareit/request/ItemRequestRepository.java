package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(nativeQuery = true, name = "ItemRequestDtosAll")
    List<ItemRequestDto> findAllItemRequestDto(@Param("id") long userId, Pageable pageable);

    @Query(nativeQuery = true, name = "ItemRequestDtosByRequesterId")
    List<ItemRequestDto> findAllByRequesterId(@Param("id") long userId, Pageable pageable);

    @Query(nativeQuery = true, name = "ItemRequestDtosById")
    Optional<ItemRequestDto> findById(@Param("id") long userId);
}
