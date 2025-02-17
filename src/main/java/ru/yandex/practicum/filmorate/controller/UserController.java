package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest newUser) {
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@Positive(message = "Id пользователя должен быть положительным числом")
                             @PathVariable("id") Long id,
                             @Positive(message = "Id друга должен быть положительным числом")
                             @PathVariable("friendId") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto removeFromFriends(@Positive(message = "Id пользователя должен быть положительным числом")
                                     @PathVariable("id") Long id,
                                     @Positive(message = "Id друга должен быть положительным числом")
                                     @PathVariable("friendId") Long friendId) {
        return userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@Positive @PathVariable("id") Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@Positive(message = "Id пользователя должен быть положительным числом")
                                          @PathVariable("id") Long id,
                                          @Positive(message = "Id пользователя должен быть положительным числом")
                                          @PathVariable("otherId") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}