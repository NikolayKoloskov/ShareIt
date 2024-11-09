package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.request.ItemRequest;


@Component("itemMapper")
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDTO toDTO(Item item) {
        return new ItemDTO().toBuilder()
                .id(item.getId())
                .name(item.getName())
                .available(String.valueOf(item.isAvailable()))
                .description(item.getDescription())
                .owner(item.getOwner())
                .build();

    }

    public ItemDTO toDTO(Item item, int ownerId) {
        return toDTO(item).toBuilder().owner(ownerId).build();
    }

    public Item toItem(ItemDTO itemDTO, int itemId, ItemRequest request) {
        return new Item().toBuilder()
                .id(itemId)
                .name(itemDTO.getName())
                .available(Boolean.parseBoolean(itemDTO.getAvailable()))
                .description(itemDTO.getDescription())
                .request(request)
                .build();
    }

    public Item toItem(ItemDTO itemDTO, int itemId, ItemRequest request, int ownerId) {
        return toItem(itemDTO, itemId, request).toBuilder().owner(ownerId).build();
    }

}
