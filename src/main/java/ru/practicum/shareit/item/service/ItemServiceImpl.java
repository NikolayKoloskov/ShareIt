package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DublicatedDataException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final Map<Integer, Item> items;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private int id;

    @Override
    public ItemDTO create(ItemDTO item) {
        id++;
        userService.getById(item.getOwner());
        Item newItem = itemMapper.toItem(item, id, new ItemRequest(), item.getOwner());
        newItem.setId(id);
        if (items.containsKey(id)) {
            id--;
            throw new DublicatedDataException("Предмет с id " + id + " уже существует");
        }
        items.put(id, newItem);
        return itemMapper.toDTO(newItem, userService.getById(item.getOwner()).getId());
    }

    @Override
    public ItemDTO update(ItemDTO item) {
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
            itemToUpdate.setAvailable(true);
        }
        items.put(itemToUpdate.getId(), itemToUpdate);
        log.info("Обновлен предмет {}", item);
        return itemMapper.toDTO(itemToUpdate);
    }

    @Override
    public ItemDTO delete(ItemDTO item) {
        return null;
    }

    @Override
    public ItemDTO findById(int id) {
        log.info("Поиск предмета по id: {}", id);
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException("Предмет с id " + id + " не найден");
        }
        return itemMapper.toDTO(items.get(id));
    }

    @Override
    public List<ItemDTO> findAll() {
        log.info("Все предметы");
        return items.values().stream().map(itemMapper::toDTO).toList();
    }

    @Override
    public List<ItemDTO> findByUserId(int userId) {
        log.info("Поиск предметов по userId: {}", userId);
        userService.getById(userId);
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(itemMapper::toDTO)
                .toList();
    }

    @Override
    public List<ItemDTO> findByText(String text) {
        log.info("Поиск предметов по тексту: {}", text);
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.isAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .peek(item -> log.trace("Подходящий предмет под описание: {}", item))
                .map(itemMapper::toDTO)
                .toList();
    }


}
