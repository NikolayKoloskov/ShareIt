package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO create(User user);

    UserDTO update(int id, User user);

    String delete(int id);

    List<UserDTO> getAll();

    UserDTO getById(int id);
}
