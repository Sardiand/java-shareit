package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start <= ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBooker_IdOrderByStartDesc(long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPreviousByBooker_IdOrderByStartDesc(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUpcomingByBooker_IdOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItem_IdInOrderByStartDesc(List<Long> itemIds);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentByItemIdsInOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.start < ?2 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPreviousByItemIdsInOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN ?1 " +
            "AND b.start >= ?2 " +
            "AND b.end >= ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUpcomingByItemIdsInOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItem_IdInAndStatusOrderByStartDesc(List<Long> itemIds, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end <= ?3 " +
            "AND b.status = ?4 " +
            "GROUP BY b.id " +
            "ORDER BY b.end DESC")
    List<Booking> findPreviousByItemIdAndUserId(long itemId, long userId, LocalDateTime now, Status status);

    @Query(value = "SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status = ?2 " +
            "AND b.start < ?3 " +
            "ORDER BY b.end DESC")
    List<Booking> findPreviousByItem_id(long itemId, Status status, LocalDateTime now);

    @Query(value = "SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status = ?2 " +
            "AND b.start > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> findUpcomingByItem_Id(long itemId, Status status, LocalDateTime now);

}
