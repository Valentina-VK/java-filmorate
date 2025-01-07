package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmTest {
    private Film film;
    private static final LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895, 12, 28);
    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("TestFilmName");
        film.setDescription("TestFilmDescription");
        film.setReleaseDate(LocalDate.of(2024, 10, 10));
        film.setDuration(200);
    }

    @Test
    void shouldPassValidationForCorrectFields() {
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertTrue(violation.isEmpty(), "Валидация при корректно заполненных полях не прошла");
    }

    @Test
    void notValidateForBadName() {
        film.setName("");
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertFalse(violation.isEmpty(), "Валидация прошла при пустом поле");
        film.setName(null);
        assertFalse(violation.isEmpty(), "Валидация прошла при значении null");
    }

    @Test
    void notValidateForDescriptionSizeMoreThen200() {
        film.setDescription("Test".repeat(200));
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertFalse(violation.isEmpty(), "Валидация прошла при превышении лимита символов");
    }

    @Test
    void notValidateForDurationNotPositive() {
        film.setDuration(-200);
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertFalse(violation.isEmpty(), "Валидация прошла при неположительном значении");
        film.setDuration(0);
        assertFalse(violation.isEmpty(), "Валидация прошла при значении null");
    }

    @Test
    void notValidateForReleaseDateEarlyThanBirthdayOfCinema() {
        film.setReleaseDate(BIRTHDAY_OF_CINEMA.minusDays(1));
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        assertFalse(violation.isEmpty(), "Валидация прошла при дате раньше заданного предела");
        film.setReleaseDate(null);
        assertFalse(violation.isEmpty(), "Валидация прошла при значении null");
    }
}