package ru.practicum.shareit.item.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Valid
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    int id;

    @NotNull(message = "Имя должно быть заполнено")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Описание должно быть заполнено")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Доступность должна быть заполнена")
    @NotEmpty(message = "Доступность не может быть пустой")
    @NotBlank(message = "Доступность не может быть пустой")
    @Pattern(regexp = "^true$|^false$", message = "allowed input: true or false")
    private String available;

    private int owner;

}
