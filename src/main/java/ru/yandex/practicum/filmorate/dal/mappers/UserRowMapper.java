package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
@AllArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final JdbcTemplate jdbc;
    private static final String FRIENDS_QUERY = "SELECT f.friend_id FROM friendship AS f WHERE f.user_id = ? " +
            "UNION SELECT fr.user_id FROM friendship AS fr WHERE fr.friend_id = ? AND status ='CONFIRMED'";

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setFriends(new HashSet<>(jdbc.queryForList(FRIENDS_QUERY, Long.class, user.getId(), user.getId())));
        return user;
    }
}