package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.validation.DateTimeStartBeforeEnd;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@DateTimeStartBeforeEnd
public class BookingSaveDto {
    @NotNull
    Integer itemId;
    @Future
    LocalDateTime start;
    LocalDateTime end;
}
