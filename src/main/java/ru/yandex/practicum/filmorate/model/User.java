package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"login", "email"})
public class User {
    private long id;
    @NotNull(message = "Email должен быть заполнен")
    @Email(message = "Email должен быть корректным")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    @Pattern(regexp = ".*\\S.*", message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
