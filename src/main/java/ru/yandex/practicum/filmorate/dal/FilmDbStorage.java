package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String INSERT_FILM_QUERY = "INSERT INTO films " +
            "(film_name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILMS_GENRES_QUERY = "INSERT INTO films_genres (film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET film_name = ?, description = ?, " +
            "release_date = ?, duration = ?, mpa_id = ?  WHERE film_id = ?";
    private static final String DELETE_FILMS_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String FIND_FILMS_QUERY = "SELECT * FROM films";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private final GenreDbStorage genreDbStorage;
    private final RatingMpaDbStorage ratingStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbStorage genreDbStorage,
                         RatingMpaDbStorage ratingStorage, UserDbStorage userDbStorage) {
        super(jdbc, mapper);
        this.genreDbStorage = genreDbStorage;
        this.ratingStorage = ratingStorage;
        this.userDbStorage = userDbStorage;
    }

    public List<Film> getFilms() {
        return findMany(FIND_FILMS_QUERY);
    }

    public Film create(Film film) {
        long filmId = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                getRatingId(film));
        film.setId(filmId);
        setGenresForFilm(film);
        return film;
    }

    public Film update(Film newFilm) {
        update(UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                getRatingId(newFilm),
                newFilm.getId());
        setGenresForFilm(newFilm);
        return newFilm;
    }

    public Film getFilmById(long id) {
        return findOne(FIND_FILM_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден, id: " + id));
    }

    public Film userLikesFilm(Long id, Long userId) {
        getFilmById(id);
        userDbStorage.getUserById(userId);
        update(INSERT_LIKE_QUERY, id, userId);
        return getFilmById(id);
    }

    public Film deleteLikesFilm(Long id, Long userId) {
        getFilmById(id);
        userDbStorage.getUserById(userId);
        update(DELETE_LIKE_QUERY, id, userId);
        return getFilmById(id);
    }

    private Long getRatingId(Film film) {
        Long ratingId = null;
        if (film.getRatingMpa() != null) {
            ratingId = film.getRatingMpa().getId();
            ratingStorage.getRatingMpaById(ratingId);
        }
        return ratingId;
    }

    private void setGenresForFilm(Film film) {
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            genres.stream()
                    .map(Genre::getId)
                    .forEach(genreDbStorage::getGenreById);
            jdbc.update(DELETE_FILMS_GENRES_QUERY, film.getId());
            genres.stream()
                    .map(Genre::getId)
                    .forEach(id -> jdbc.update(INSERT_FILMS_GENRES_QUERY, film.getId(), id));
        }
    }
}