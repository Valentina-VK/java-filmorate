package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> getUsers() {
        return UserMapper.toDto(userStorage.getUsers());
    }

    public UserDto getUserById(long id) {
        return UserMapper.toDto(userStorage.getUserById(id));
    }

    public UserDto createUser(NewUserRequest user) {
        return UserMapper.toDto(userStorage.create(UserMapper.toEntity(user)));
    }

    public UserDto updateUser(UpdateUserRequest newUser) {
        User user = userStorage.getUserById(newUser.getId());
        return UserMapper.toDto(UserMapper.toEntity(user, newUser));
    }

    public UserDto addFriend(long id, long friendId) {
        User user = userStorage.addFriend(id, friendId);
        return UserMapper.toDto(user);
    }

    public UserDto removeFromFriends(long id, long friendId) {
        User user = userStorage.deleteFriend(id, friendId);
        return UserMapper.toDto(user);
    }

    public List<UserDto> getFriends(long id) {
        return UserMapper.toDto(userStorage.listOfFriends(id));
    }

    public List<UserDto> getCommonFriends(long id, long otherId) {
        return UserMapper.toDto(userStorage.listOfCommonFriends(id, otherId));
    }
}