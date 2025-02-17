package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenreById(long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден, id: " + id));
    }
}