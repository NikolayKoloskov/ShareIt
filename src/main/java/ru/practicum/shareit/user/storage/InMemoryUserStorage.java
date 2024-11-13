package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DublicatedDataException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("inMemoryUserStorage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InMemoryUserStorage implements UserStorage {

    private final UserMapper userMapper;
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User createUser(UserDTO user) {
        id++;
        user.setId(id);
        User newUser = userMapper.toUser(user);
        log.info("Создание пользователя {}", user);
        if (users.containsKey(newUser.getId())) {
            id--;
            throw new DublicatedDataException("Пользователь с id " + newUser.getId() + " уже существует");
        }
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DublicatedDataException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        users.put(user.getId(), newUser);
        log.info("Пользователь создан {}", newUser);
        return newUser;
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        User userToUpdate = users.get(user.getId());
        log.info("Обновление пользователя {} на {}", userToUpdate, user);
        if (user.getEmail() != null) {
            if (users.values().stream()
                    .filter(u -> u.getId() != userToUpdate.getId())
                    .map(User::getEmail)
                    .anyMatch(email -> email.equals(user.getEmail()))
            ) {
                throw new DublicatedDataException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            userToUpdate.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        users.put(userToUpdate.getId(), userToUpdate);
        log.info("Пользователь обновлен {}", userToUpdate);
        return userToUpdate;
    }

    @Override
    public String deleteUser(int id) {
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
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new UserNotFoundException("User with id " + id + " not found");
    }
}
