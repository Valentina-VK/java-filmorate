package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User create(User user);

    User update(User newUser);

    User getUserById(long id);

    User addFriend(long userId, long otherId);

    User deleteFriend(long userId, long friendId);

    List<User> listOfFriends(long id);

    List<User> listOfCommonFriends(Long id, Long otherId);
}