package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "ExceptionResolver")
@RestControllerAdvice
@ControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseMessage handleUserNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponseMessage("User not found", e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseMessage handleItemNotFoundException(ItemNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponseMessage("Item not found", e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseMessage handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error(e.getMessage());
        return new ErrorResponseMessage("Не передан Header X-Sharer-User-Id", e.getMessage());
    }


    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponseMessage handleForbiddenException(ForbiddenException e) {
        return new ErrorResponseMessage("Доступ запрещен", e.getMessage());
    }

    @ExceptionHandler(NotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseMessage notValidExceptionHandler(NotValidException e) {
        log.error("not valid exception - {} ({})", e.getMessage(), e.getStackTrace()[0].toString());
        return new ErrorResponseMessage("BadRequest", e.getMessage());
    }
}
