package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper
public interface ItemRequestMapper {

    ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest);
}
