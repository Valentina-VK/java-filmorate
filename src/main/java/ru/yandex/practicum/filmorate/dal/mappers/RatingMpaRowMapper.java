package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingMpaRowMapper implements RowMapper<RatingMpa> {

    @Override
    public RatingMpa mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new RatingMpa(resultSet.getLong("mpa_id"), resultSet.getString("mpa_name"));
    }
}