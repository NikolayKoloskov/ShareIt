package ru.practicum.shareit.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.api.RequestHttpHeaders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ExceptionResolverTest {
    private final ObjectMapper objectMapper;
    private final BookingController bookingController;
    private BookingDto bookingExpected;

    @Test
    void handleItemNotFoundExceptionTest() throws Exception {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(555);
        bookingSaveDto.setStart(LocalDateTime.now());
        bookingSaveDto.setEnd(LocalDateTime.now().plusMinutes(1));
        String bookingSaveDtoJson = objectMapper.writeValueAsString(bookingSaveDto);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionResolver())
                .build();

        mockMvc.perform(post("/bookings")
                        .header(RequestHttpHeaders.USER_ID, String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingSaveDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(content().json(String.format("{\"error\":\"Item not found\", \"message\": \"Предмет с ID 555 не найден\"}")));
    }

    @Test
    void handleUserNotFoundException() throws Exception {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(10);
        bookingSaveDto.setStart(LocalDateTime.now());
        bookingSaveDto.setEnd(LocalDateTime.now().plusMinutes(1));
        String bookingSaveDtoJson = objectMapper.writeValueAsString(bookingSaveDto);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionResolver())
                .build();

        mockMvc.perform(post("/bookings")
                        .header(RequestHttpHeaders.USER_ID, String.valueOf(999))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingSaveDtoJson))
                .andExpect(status().isForbidden())
                .andExpect(content().json(String.format("{\"error\":\"Доступ запрещен\", \"message\": \"Пользователь с ID 999 не найден\"}")));
    }

    @Test
    void handleBadRequestException() {
    }

    @Test
    void handleMissingRequestHeaderExceptionTest() throws Exception {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(10);
        bookingSaveDto.setStart(LocalDateTime.now());
        bookingSaveDto.setEnd(LocalDateTime.now().plusMinutes(1));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionResolver())
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 10,\"start\":\"2024-12-11T22:27:47\",\"end\":\"2024-12-11T22:27:48\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(String.format("{\"error\":\"Не передан Header X-Sharer-User-Id\", \"message\": \"Required request header 'X-Sharer-User-Id' for method parameter type int is not present\"}")));

    }

    @Test
    void handleConstraintViolationException() {
    }

    @Test
    void handleForbiddenException() {
    }

    @Test
    void notValidExceptionHandler() throws Exception {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(10);
        bookingSaveDto.setStart(LocalDateTime.now());
        bookingSaveDto.setEnd(LocalDateTime.now().plusMinutes(1));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionResolver())
                .build();

        mockMvc.perform(post("/bookings")
                        .header(RequestHttpHeaders.USER_ID, String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 50,\"start\":\"2024-12-11T22:27:47\",\"end\":\"2024-12-11T22:27:48\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(String.format("{\"error\":\"BadRequest\", \"message\": \"Item Не доступно для бронирования\"}")));
    }
}