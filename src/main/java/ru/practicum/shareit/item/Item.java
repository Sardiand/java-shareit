package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Entity
@Table(name = "items", schema = "public")
@NamedNativeQueries({
        @NamedNativeQuery(name = "ItemBookingDtos",
                query = "WITH dto AS (SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.owner_id AS ownerId " +
                        "FROM items AS i WHERE i.owner_id = ?1), " +

                        "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
                        "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
                        "FULL JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
                        "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
                        "AS rank_lb FROM bookings AS b WHERE b.status = 'APPROVED' " +
                        "AND b.start_date <= now()) AS lb ON dto.id = lb.item_id) " +

                        "SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
                        "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
                        "nb.booker_id AS nextBookerId FROM dtolb FULL JOIN (SELECT b.id, b.booker_id, " +
                        "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
                        "FROM bookings AS b WHERE b.status = 'APPROVED' AND b.start_date >= now()) AS nb " +
                        "ON dtolb.id = nb.item_id WHERE (dtolb.rank_lb = 1 OR dtolb.rank_lb IS NULL) " +
                        "AND (nb.rank_nb = 1 OR nb.rank_nb is NULL) " +
                        "ORDER BY dtolb.id ASC",
                resultSetMapping = "ItemBookingDtoMapping"),

        @NamedNativeQuery(name = "ItemBDByID",
                query = "WITH dto AS (SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.owner_id AS ownerId " +
                        "FROM items AS i WHERE i.id = ?1), " +

                        "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
                        "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
                        "FULL JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
                        "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
                        "AS rank_lb FROM bookings AS b WHERE b.item_id = ?1 AND b.status = 'APPROVED' " +
                        "AND b.start_date <= now()) AS lb ON dto.id = lb.item_id) " +

                        "SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
                        "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
                        "nb.booker_id AS nextBookerId FROM dtolb FULL JOIN (SELECT b.id, b.booker_id, " +
                        "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
                        "FROM bookings AS b WHERE b.item_id = ?1 AND b.status = 'APPROVED' AND b.start_date >= now()) AS nb " +
                        "ON dtolb.id = nb.item_id WHERE (dtolb.rank_lb = 1 OR dtolb.rank_lb IS NULL) " +
                        "AND (nb.rank_nb = 1 OR nb.rank_nb is NULL) " +
                        "ORDER BY dtolb.id ASC",
                resultSetMapping = "ItemBookingDtoMapping")})
@SqlResultSetMapping(name = "ItemBookingDtoMapping", classes = {
        @ConstructorResult(columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "name"),
                @ColumnResult(name = "description"),
                @ColumnResult(name = "available", type = boolean.class),
                @ColumnResult(name = "ownerId", type = Long.class),
                @ColumnResult(name = "lastBookingId", type = Long.class),
                @ColumnResult(name = "lastBookerId", type = Long.class),
                @ColumnResult(name = "nextBookingId", type = Long.class),
                @ColumnResult(name = "nextBookerId", type = Long.class)},
                targetClass = ItemBookingDto.class)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле name не может быть пустым.")
    @NonNull
    @Size
    private String name;

    @NonNull
    @NotBlank(message = "Поле description не может быть пустым.")
    private String description;

    @NonNull
    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(@NonNull String name, @NonNull String description, @NonNull Boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.available = isAvailable;
    }
}
