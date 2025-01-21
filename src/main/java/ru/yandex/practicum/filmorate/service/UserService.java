package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User newUser) {
        return userStorage.update(newUser);
    }

    public User addFriend(long id, long friendId) {
        User user = userStorage.getUserById(id);
        user.addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(id);
        return user;
    }

    public User removeFromFriends(long id, long friendId) {
        User user = userStorage.getUserById(id);
        user.removeFriend(friendId);
        userStorage.getUserById(friendId).removeFriend(id);
        return user;
    }

    public List<User> getFriends(long id) {
        return userStorage.getUserById(id)
                .getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> firstUserFriends = userStorage.getUserById(id)
                .getFriends();
        return userStorage.getUserById(otherId)
                .getFriends().stream()
                .filter(firstUserFriends::contains)
                .map(userStorage::getUserById)
                .toList();
    }
}