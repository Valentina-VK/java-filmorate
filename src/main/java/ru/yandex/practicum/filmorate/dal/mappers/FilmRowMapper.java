package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
@AllArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreMapper;
    private final RowMapper<RatingMpa> ratingMapper;

    private static final String GENRES_OF_FILM_QUERY = "SELECT g.genre_id, g.genre_name FROM genres AS g " +
            "JOIN films_genres AS fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";
    private static final String LIKES_OF_FILM_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String RATING_MPA_OF_FILM_QUERY = "SELECT r.mpa_id, r.mpa_name FROM rating_mpa AS r " +
            "WHERE r.mpa_id = (SELECT mpa_id FROM films WHERE film_id = ?)";

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        long id = resultSet.getLong("film_id");
        film.setId(id);
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setGenres(new HashSet<>(jdbc.query(GENRES_OF_FILM_QUERY, genreMapper, id)));
        film.setLikes(new HashSet<>(jdbc.queryForList(LIKES_OF_FILM_QUERY, Long.class, id)));
        film.setNumberOfLikes(film.getLikes().size());
        film.setRatingMpa(jdbc.queryForObject(RATING_MPA_OF_FILM_QUERY, ratingMapper, id));
        return film;
    }
}