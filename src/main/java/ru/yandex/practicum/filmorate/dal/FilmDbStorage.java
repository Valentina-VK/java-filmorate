package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private static final String FIND_FILMS_QUERY = "SELECT f.*, r.mpa_name FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.mpa_id";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT f.*, r.mpa_name FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.mpa_id WHERE f.film_id = ? ";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FILMS_GENRES_QUERY = "SELECT f.film_id, f.genre_id, g.genre_name FROM films_genres AS f " +
            "JOIN genres AS g ON f.genre_id = g.genre_id ORDER BY f.genre_id";
    private static final String FIND_GENRES_BY_FILM_QUERY = "SELECT f.film_id, f.genre_id, g.genre_name FROM films_genres AS f " +
            "JOIN genres AS g ON f.genre_id = g.genre_id WHERE f.film_id = ? ORDER BY f.genre_id";
    private static final String FIND_LIKES_BY_FILM_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String GET_POPULAR_QUERY = "SELECT f.*, r.mpa_name FROM films AS f " +
            "LEFT JOIN rating_mpa AS r ON f.mpa_id = r.mpa_id " +
            "RIGHT JOIN (SELECT film_id, COUNT(user_id) as c FROM likes GROUP BY film_id ORDER BY c DESC LIMIT ?) as l " +
            "ON l.film_id = f.film_id";

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
        List<Film> allFilms = findMany(FIND_FILMS_QUERY);
        return setGenres(allFilms);
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
        Film film = findOne(FIND_FILM_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден, id: " + id));
        Map<Long, Set<Genre>> genres = jdbc.query(FIND_GENRES_BY_FILM_QUERY, this::extractData, id);
        if (genres != null)
            film.setGenres(genres.get(film.getId()));
        Set<Long> likes = Set.copyOf(jdbc.queryForList(FIND_LIKES_BY_FILM_QUERY, Long.class, id));
        if (!likes.isEmpty()) film.setLikes(likes);
        return film;
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

    public List<Film> getPopularFilms(int count) {
        List<Film> popFilms = findMany(GET_POPULAR_QUERY, count);
        setGenres(popFilms);
        return popFilms;
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
        if (genres != null && !genres.isEmpty()) {
            List<Long> genresInBase = genreDbStorage.getGenres().stream().map(Genre::getId).toList();
            if (genres.stream()
                    .map(Genre::getId)
                    .allMatch(genresInBase::contains)) {
                List<Object[]> batchArgs = new ArrayList<>();
                jdbc.update(DELETE_FILMS_GENRES_QUERY, film.getId());
                for (Genre genre : genres) {
                    batchArgs.add(new Object[]{film.getId(), genre.getId()});
                }
                jdbc.batchUpdate(INSERT_FILMS_GENRES_QUERY, batchArgs);
            } else throw new NotFoundException("Жанр не найден");
        }
    }

    private List<Film> setGenres(List<Film> films) {
        Map<Long, Set<Genre>> filmsGenres = getFilmsGenres();
        for (Film film : films) {
            if (filmsGenres.get(film.getId()) != null)
                film.setGenres(filmsGenres.get(film.getId()));
        }
        return films;
    }


    private Map<Long, Set<Genre>> getFilmsGenres() {
        return jdbc.query(FILMS_GENRES_QUERY, this::extractData);
    }

    private Map<Long, Set<Genre>> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
        Map<Long, Set<Genre>> data = new LinkedHashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            data.putIfAbsent(filmId, new HashSet<>());
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            data.get(filmId).add(genre);
        }
        return data;
    }
}