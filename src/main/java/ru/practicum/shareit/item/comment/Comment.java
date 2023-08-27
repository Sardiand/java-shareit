package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@RequiredArgsConstructor
@NoArgsConstructor
@Data
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
