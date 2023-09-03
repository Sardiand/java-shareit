package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemCommentQueries;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentItemDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@NamedNativeQueries({
        @NamedNativeQuery(name = "CommentDtos", query = ItemCommentQueries.queryCommentDto,
                resultSetMapping = "CommentDtoMapping"),
        @NamedNativeQuery(name = "CommentItemDtos", query = ItemCommentQueries.queryCommentItemDto,
                resultSetMapping = "CommentItemDtoMapping")})
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "CommentDtoMapping", classes = {
                @ConstructorResult(columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "text"),
                        @ColumnResult(name = "created", type = LocalDateTime.class),
                        @ColumnResult(name = "authorName")
                },
                        targetClass = CommentDto.class)
        }),
        @SqlResultSetMapping(name = "CommentItemDtoMapping", classes = {
                @ConstructorResult(columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "text"),
                        @ColumnResult(name = "created", type = LocalDateTime.class),
                        @ColumnResult(name = "authorName"),
                        @ColumnResult(name = "itemId", type = Long.class)
                },
                        targetClass = CommentItemDto.class)
        })})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    private String text;

    @NonNull
    private LocalDateTime created;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "author_id")
    private User author;
}
