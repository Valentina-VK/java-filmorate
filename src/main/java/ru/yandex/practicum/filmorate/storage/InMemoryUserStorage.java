package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id: {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с указанным id не найден: " + newUser.getId());
        }
        User oldUser = users.get(newUser.getId());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setName(newUser.getName());
        log.info("Данные пользователя обновлены {}", oldUser);
        return oldUser;
    }

    @Override
    public User getUserById(long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден, id: " + id));
    }

    @Override
    public User addFriend(long userId, long otherId) {
        User user = getUserById(userId);
        user.addFriend(otherId);
        getUserById(otherId).addFriend(userId);
        return user;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        User user = getUserById(userId);
        user.removeFriend(friendId);
        getUserById(friendId).removeFriend(userId);
        return user;
    }

    @Override
    public List<User> listOfFriends(long id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> listOfCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = listOfFriends(id);
        listOfFriends(id).retainAll(listOfFriends(otherId));
        return commonFriends;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}