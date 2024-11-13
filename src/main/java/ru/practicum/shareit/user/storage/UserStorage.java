package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;

import java.util.List;

public interface UserStorage {
    User createUser(UserDTO user);

    User getUserById(int id);

    User updateUser(User user);

    String deleteUser(int id);

    List<User> getAll();

    User getById(int id);
}
