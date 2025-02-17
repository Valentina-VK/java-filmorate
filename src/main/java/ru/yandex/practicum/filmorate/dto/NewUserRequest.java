package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @NotNull(message = "Email должен быть заполнен")
    @Email(message = "Email должен быть корректным")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    @Pattern(regexp = ".*\\S.*", message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public void setLogin(String login) {
        this.login = login;
        if (this.name == null || this.name.isBlank()) {
            this.name = this.login;
        }
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = this.login;
        } else {
            this.name = name;
        }
    }
}
