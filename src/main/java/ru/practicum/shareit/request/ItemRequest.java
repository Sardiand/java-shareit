package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Data
@NoArgsConstructor
@NamedNativeQueries({@NamedNativeQuery(name = "ItemRequestDtosAll", query =
        "SELECT r.id, r.description, r.created " +
                "FROM requests AS r WHERE r.requester_id != :id ORDER BY r.created DESC ",
        resultSetMapping = "ItemRequestDtoMapper"),

        @NamedNativeQuery(name = "ItemRequestDtosById", query =
                "SELECT r.id, r.description, r.created " +
                        "FROM requests AS r WHERE r.id = :id ORDER BY r.created DESC ",
                resultSetMapping = "ItemRequestDtoMapper"),

        @NamedNativeQuery(name = "ItemRequestDtosByRequesterId", query =
                "SELECT r.id, r.description, r.created " +
                        "FROM requests AS r WHERE r.requester_id = :id " +
                        "ORDER BY r.created DESC ",
                resultSetMapping = "ItemRequestDtoMapper")})

@SqlResultSetMapping(name = "ItemRequestDtoMapper", classes = {
        @ConstructorResult(columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "description"),
                @ColumnResult(name = "created", type = LocalDateTime.class)},
                targetClass = ItemRequestDto.class)})

public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    private LocalDateTime created;

    public ItemRequest(String description, User requester, LocalDateTime created) {
        this.description = description;
        this.requester = requester;
        this.created = created;
    }
}
