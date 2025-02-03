package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserSaveDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperImplTest {
    @Test
    void mapTest() {
        User user = null;
        UserMapperImpl mapper = new UserMapperImpl();
        assertNull(mapper.map(user));
    }

    @Test
    void testMapTest() {
        UserSaveDto user = null;
        UserMapperImpl mapper = new UserMapperImpl();
        assertNull(mapper.map(user));
    }

}