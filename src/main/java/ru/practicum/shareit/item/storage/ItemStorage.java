package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;

import java.util.List;

public interface ItemStorage {
    Item create(ItemDTO itemDTO);

    Item update(ItemDTO itemDTO);

    Item findById(int id);

    List<Item> findAll();

    List<Item> findByUserId(int userId);

    List<Item> findByText(String text);
}
