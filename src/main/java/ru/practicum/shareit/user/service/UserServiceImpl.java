package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.UserDTO;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserStorage userStorage;


    @Override
    public UserDTO create(UserDTO user) {
        return userMapper.toDTO(userStorage.createUser(user));
    }

    @Override
    public UserDTO update(int id, UserDTO user) {
        user.setId(id);
        return userMapper.toDTO(userStorage.updateUser(userMapper.toUser(user)));
    }

    @Override
    public String delete(int id) {
        return userStorage.deleteUser(id);
    }

    @Override
    public List<UserDTO> getAll() {
        return userStorage.getAll().stream().map(userMapper::toDTO).toList();
    }

    @Override
    public UserDTO getById(int id) {
        return userMapper.toDTO(userStorage.getById(id));
    }
}
