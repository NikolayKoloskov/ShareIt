package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {

    private int id;

    @NotNull(message = "Поле name должно быть заполнено")
    @NotBlank(message = "Name не должен быть пустым")
    private String name;

    @NotNull(message = "Поле Email должно быть заполнено")
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Формат Email не верный")
    private String email;


}
