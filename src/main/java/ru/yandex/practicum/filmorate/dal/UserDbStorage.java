package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.ValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status) " +
            "VALUES (?, ?, 'UNCONFIRMED')";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE friendship SET status = 'CONFIRMED' " +
            "WHERE friend_id = ? AND user_id =?";
    private static final String FIND_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ? CASCADE";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?) " +
            "OR (friend_id = ? AND user_id = ?)";
    private static final String FRIENDS_QUERY = "SELECT f.friend_id FROM friendship AS f WHERE f.user_id = ? " +
            "UNION SELECT fr.user_id FROM friendship AS fr WHERE fr.friend_id = ? AND status ='CONFIRMED'";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> getUsers() {
        return findMany(FIND_USERS_QUERY);
    }

    public User create(User user) {
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    public User update(User newUser) {
        update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );
        return newUser;
    }

    public User getUserById(long userId) {
        User user = findOne(FIND_USER_BY_ID_QUERY, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден, id: " + userId));
        List<Long> friendsId = jdbc.queryForList(FRIENDS_QUERY, Long.class, user.getId(), user.getId());
        if (!friendsId.isEmpty())
            user.setFriends(Set.copyOf(friendsId));
        return user;
    }

    public void delete(long userId) {
        update(DELETE_USER_QUERY, userId);
    }

    public List<User> listOfFriends(long id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    public List<User> listOfCommonFriends(Long id, Long otherId) {
        Set<Long> commonFriends = getUserById(id).getFriends();
        Set<Long> othersFriends = getUserById(otherId).getFriends();
        return commonFriends.stream()
                .filter(othersFriends::contains)
                .map(this::getUserById)
                .toList();
    }

    public User addFriend(long userId, long otherId) {
        if (userId == otherId) throw new ValidationException("Id друга должен отличаться от id пользователя");
        Set<Long> userFriends = getUserById(userId).getFriends();
        Set<Long> otherFriends = getUserById(otherId).getFriends();
        if (otherFriends.contains(userId)) {
            jdbc.update(CONFIRM_FRIEND_QUERY, userId, otherId);
            return getUserById(userId);
        } else if (!userFriends.contains(otherId)) {
            jdbc.update(INSERT_FRIEND_QUERY, userId, otherId);
            return getUserById(userId);
        } else {
            return getUserById(userId);
        }
    }

    public User deleteFriend(long userId, long friendId) {
        Set<Long> friends = getUserById(userId).getFriends();
        getUserById(friendId);
        if (friends.contains(friendId))
            jdbc.update(DELETE_FRIEND_QUERY, userId, friendId, userId, friendId);
        return getUserById(userId);
    }
}