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
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto createUser(NewUserRequest user) {
        return UserMapper.mapToUserDto(userStorage.create(UserMapper.mapToUser(user)));
    }

    public UserDto updateUser(UpdateUserRequest newUser) {
        User user = userStorage.getUserById(newUser.getId());
        return UserMapper.mapToUserDto(UserMapper.updateUserFields(user, newUser));
    }

    public UserDto addFriend(long id, long friendId) {
        User user = userStorage.addFriend(id, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto removeFromFriends(long id, long friendId) {
        User user = userStorage.deleteFriend(id, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getFriends(long id) {
        return userStorage.listOfFriends(id).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getCommonFriends(long id, long otherId) {
        return userStorage.listOfCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}