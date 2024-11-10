package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DublicatedDataException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public UserDTO create(User user) {
        id++;
        user.setId(id);
        log.info("Создание пользователя {}", user);
        if (users.containsKey(user.getId())) {
            id--;
            throw new DublicatedDataException("Пользователь с id " + user.getId() + " уже существует");
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DublicatedDataException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        users.put(user.getId(), user);
        log.info("Пользователь создан {}", user);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO update(int id, User user) {
        user.setId(id);
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        User userToUpdate = users.get(id);
        log.info("Обновление пользователя {} на {}", userToUpdate, user);
        if (user.getEmail() != null) {
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new DublicatedDataException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            userToUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        users.put(userToUpdate.getId(), userToUpdate);
        log.info("Пользователь обновлен {}", user);
        return userMapper.toDTO(userToUpdate);
    }

    @Override
    public String delete(int id) {
        log.info("Удаление пользователя с id {}", id);
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        users.remove(id);
        if (!users.containsKey(id)) {
            return "{\n \"message\": \"Пользователь удален\" \n}";
        }
        return "{\n \"message\": \"Пользователь не удален\" \n}";
    }

    @Override
    public List<UserDTO> getAll() {
        return users.values()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDTO getById(int id) {
        if (users.containsKey(id)) {
            return userMapper.toDTO(users.get(id));
        }

        throw new UserNotFoundException("User with id " + id + " not found");
    }
}
