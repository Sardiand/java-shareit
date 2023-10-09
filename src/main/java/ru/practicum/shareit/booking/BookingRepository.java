package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBookerId(long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPreviousByBookerId(long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUpcomingByBookerId(long bookerId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_IdInOrderByStartDesc(List<Long> itemIds, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentByItemIds(List<Long> itemIds, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPreviousByItemIds(List<Long> itemIds, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUpcomingByItemIds(List<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItem_IdInAndStatusOrderByStartDesc(List<Long> itemIds, Status status, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "AND b.status = ?3 " +
            "GROUP BY b.id " +
            "ORDER BY b.end DESC")
    List<Booking> findPreviousByItemIdAndUserId(long itemId, long userId, Status status);
}
