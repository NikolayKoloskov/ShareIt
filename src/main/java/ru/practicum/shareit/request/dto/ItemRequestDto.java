package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemRequestDto {
    @NotNull(message = "Название не может быть пустым")
    private String description;
    @NotNull(message = "Юзер не может быть пустым")
    private User requester;
}
