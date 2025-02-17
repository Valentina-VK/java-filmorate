package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.List;

@Repository
public class RatingMpaDbStorage extends BaseDbStorage<RatingMpa> implements RatingMpaStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating_mpa WHERE mpa_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating_mpa ORDER BY mpa_id";

    public RatingMpaDbStorage(JdbcTemplate jdbc, RowMapper<RatingMpa> mapper) {
        super(jdbc, mapper);
    }

    public List<RatingMpa> getRatingMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    public RatingMpa getRatingMpaById(long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден, id: " + id));
    }
}