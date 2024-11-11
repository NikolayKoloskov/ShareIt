package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO create(UserDTO user);

    UserDTO update(int id, UserDTO user);

    String delete(int id);

    List<UserDTO> getAll();

    UserDTO getById(int id);
}
