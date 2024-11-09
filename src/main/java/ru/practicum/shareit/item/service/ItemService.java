package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO create(ItemDTO item);

    ItemDTO update(ItemDTO item);

    ItemDTO delete(ItemDTO item);

    ItemDTO findById(int id);

    List<ItemDTO> findAll();

    List<ItemDTO> findByUserId(int userId);

    List<ItemDTO> findByText(String text);
}
