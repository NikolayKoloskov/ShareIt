package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
public class ItemRequest {

    private int id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
