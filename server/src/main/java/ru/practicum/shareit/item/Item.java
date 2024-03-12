package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
                        "FROM items AS i), " +

                        "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
                        "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
                        "LEFT JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
                        "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
                        "AS rank_lb FROM bookings AS b WHERE b.status = 'APPROVED' " +
                        "AND b.start_date <= CURRENT_TIMESTAMP) AS lb ON dto.id = lb.item_id " +
                        "WHERE lb.rank_lb = 1 OR lb.rank_lb IS NULL), " +

                        "itemdto AS (SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
                        "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
                        "nb.booker_id AS nextBookerId FROM dtolb LEFT JOIN (SELECT b.id, b.booker_id, " +
                        "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
                        "FROM bookings AS b WHERE b.status = 'APPROVED' AND b.start_date >= CURRENT_TIMESTAMP) AS nb " +
                        "ON dtolb.id = nb.item_id WHERE nb.rank_nb = 1 OR nb.rank_nb IS NULL) " +

                        "SELECT * FROM itemdto WHERE itemdto.ownerId = :id ORDER BY itemdto.id ASC",
                resultSetMapping = "ItemBookingDtoMapping"),

        @NamedNativeQuery(name = "ItemBDByID",
                query = "WITH dto AS (SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.owner_id AS ownerId " +
                        "FROM items AS i), " +

                        "dtolb AS (SELECT dto.*, lb.lastBookingId, " +
                        "lb.booker_id AS lastBookerId, lb.rank_lb FROM dto " +
                        "LEFT JOIN (SELECT b.id as lastBookingId, b.booker_id, b.item_id, " +
                        "RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date DESC) " +
                        "AS rank_lb FROM bookings AS b WHERE b.status = 'APPROVED' " +
                        "AND b.start_date <= CURRENT_TIMESTAMP) AS lb ON dto.id = lb.item_id " +
                        "WHERE lb.rank_lb = 1 OR lb.rank_lb IS NULL), " +

                        "itemdto AS (SELECT dtolb.id, dtolb.name, dtolb.description, dtolb.available, dtolb.ownerId, " +
                        "dtolb.lastBookingId, dtolb.lastBookerId, nb.id AS nextBookingId, " +
                        "nb.booker_id AS nextBookerId FROM dtolb LEFT JOIN (SELECT b.id, b.booker_id, " +
                        "b.item_id, RANK () OVER (PARTITION BY b.item_id ORDER BY b.start_date ASC) AS rank_nb " +
                        "FROM bookings AS b WHERE b.status = 'APPROVED' " +
                        "AND b.start_date >= CURRENT_TIMESTAMP) AS nb ON dtolb.id = nb.item_id " +
                        "WHERE nb.rank_nb = 1 OR nb.rank_nb IS NULL) " +

                        "SELECT * FROM itemdto WHERE itemdto.id = :id ORDER BY itemdto.id ASC",
                resultSetMapping = "ItemBookingDtoMapping"),

        @NamedNativeQuery(name = "ItemForRequestDtosById",
                query = "SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.request_id AS requestId " +
                        "FROM items AS i WHERE i.request_id = :id ORDER BY i.id ASC",
                resultSetMapping = "ItemForRequestDtoMapping"),

        @NamedNativeQuery(name = "ItemForRequestDtosAll",
                query = "SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.request_id AS requestId " +
                        "FROM items AS i LEFT JOIN requests AS r ON i.request_id = r.id " +
                        "WHERE i.request_id IS NOT NULL " +
                        "AND r.requester_id != :id ORDER BY i.id ASC",
                resultSetMapping = "ItemForRequestDtoMapping"),

        @NamedNativeQuery(name = "ItemForRequestDtosByRequester",
                query = "SELECT i.id, i.name, i.description, " +
                        "i.is_available AS available, i.request_id AS requestId " +
                        "FROM items AS i LEFT JOIN requests AS r ON i.request_id = r.id " +
                        "WHERE r.requester_id = :id ORDER BY i.id ASC",
                resultSetMapping = "ItemForRequestDtoMapping")
})
@SqlResultSetMappings({@SqlResultSetMapping(name = "ItemBookingDtoMapping", classes = {
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
}),
        @SqlResultSetMapping(name = "ItemForRequestDtoMapping", classes = {
                @ConstructorResult(columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "description"),
                        @ColumnResult(name = "available", type = boolean.class),
                        @ColumnResult(name = "requestId", type = Long.class)},
                        targetClass = ItemDto.class)
        })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public Item(@NonNull String name, @NonNull String description, @NonNull Boolean isAvailable, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = isAvailable;
        this.request = request;
    }
}
