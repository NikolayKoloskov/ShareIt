package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DublicatedDataException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "InMemoryItemStorage")
@Component("inMemoryItemStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InMemoryItemStorage implements ItemStorage {
    private Map<Integer, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private int id = 0;

    @Override
    public Item create(ItemDTO item) {
        id++;
        Item newItem = itemMapper.toItem(item, id, new ItemRequest(), item.getOwner());
        if (items.containsKey(id)) {
            id--;
            throw new DublicatedDataException("Предмет с id " + id + " уже существует");
        }
        items.put(id, newItem);
        return items.get(id);
    }

    @Override
    public Item update(ItemDTO item) {
        if (!items.containsKey(item.getId())) {
            throw new ItemNotFoundException("Предмет с id " + item.getId() + " не найден");
        }
        if (items.get(item.getId()).getOwner() != item.getOwner()) {
            throw new ForbiddenException("Нельзя редактировать предметы другого пользователя");
        }
        log.info("Обновляем предмет {}", item);
        Item itemToUpdate = items.get(item.getId());
        if (item.getName() != null && !itemToUpdate.getName().equals(item.getName())) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !itemToUpdate.getDescription().equals(item.getDescription())) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && !String.valueOf(itemToUpdate.isAvailable()).equals(item.getAvailable())) {
            if (item.getAvailable().equals("false")) {
                itemToUpdate.setAvailable(false);
            }
            if (item.getAvailable().equals("true")) {
                itemToUpdate.setAvailable(true);
            }
        }
        items.put(itemToUpdate.getId(), itemToUpdate);
        log.info("Обновлен предмет {}", itemToUpdate);
        return itemToUpdate;
    }

    @Override
    public Item findById(int id) {
        log.info("Поиск предмета по id: {}", id);
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Предмет с id " + id + " не найден");
        }
        return items.get(id);
    }

    @Override
    public List<Item> findByUserId(int userId) {
        log.info("Поиск предметов по userId: {}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public List<Item> findByText(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .peek(item -> log.trace("Подходящий предмет под описание: {}", item))
                .toList();
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
