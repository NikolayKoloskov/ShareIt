package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private int id;

    @Override
    public ItemDTO create(ItemDTO item) {
        userService.getById(item.getOwner());
        return itemMapper.toDTO(itemStorage.create(item), userService.getById(item.getOwner()).getId());
    }

    @Override
    public ItemDTO update(ItemDTO item) {
        return itemMapper.toDTO(itemStorage.update(item));
    }


    @Override
    public ItemDTO findById(int id) {
        return itemMapper.toDTO(itemStorage.findById(id));
    }

    @Override
    public List<ItemDTO> findAll() {
        itemStorage.findAll();
        return itemStorage.findAll().stream().map(itemMapper::toDTO).toList();
    }

    @Override
    public List<ItemDTO> findByUserId(int userId) {
        log.info("Поиск предметов по userId: {}", userId);
        userService.getById(userId);
        return itemStorage.findByUserId(userId).stream().map(itemMapper::toDTO).toList();
    }

    @Override
    public List<ItemDTO> findByText(String text) {
        log.info("Поиск предметов по тексту: {}", text);
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.findByText(text).stream().map(itemMapper::toDTO).toList();
    }


}
