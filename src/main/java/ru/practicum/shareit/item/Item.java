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

import static ru.practicum.shareit.item.ItemCommentQueries.queryForIBDByItemId;
import static ru.practicum.shareit.item.ItemCommentQueries.queryForItemBookingDto;

@Entity
@Table(name = "items", schema = "public")
@NamedNativeQueries({
        @NamedNativeQuery(name = "ItemBookingDtos",
                query = queryForItemBookingDto,
                resultSetMapping = "ItemBookingDtoMapping"),
        @NamedNativeQuery(name = "ItemBDByID",
                query = queryForIBDByItemId,
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
