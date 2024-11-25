package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 300)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requester;

    private LocalDateTime created = LocalDateTime.now();
}
