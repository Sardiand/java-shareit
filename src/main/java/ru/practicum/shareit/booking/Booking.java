package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name = "start_date")
    private LocalDateTime start;

    @NonNull
    @Column(name = "end_date")
    private LocalDateTime end;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;
}
