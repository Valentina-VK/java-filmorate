package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    // во всех обработчиках замените формат ответа на ErrorResponse
    // добавьте коды ошибок
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleHappinessOverflow(final ValidationException e) {
        return new ErrorResponse("Некорректный параметр в запросе: ", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleIncorrectParameter(final NotFoundException e) {
        return new ErrorResponse("Указанный Id не найден: ", e.getMessage());
    }

    // реализуйте обработчик UnauthorizedUserException
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleUnauthorizedUser(final RuntimeException e) {
        return new ErrorResponse("Ошибка в работе сервера.", e.getMessage());
    }
}