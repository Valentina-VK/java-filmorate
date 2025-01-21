package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film addLikeToFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        filmStorage.getFilmById(filmId).addLike(userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film removeLikeFromFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        filmStorage.getFilmById(filmId).removeLike(userId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getNumberOfLikes).reversed())
                .limit(count).toList();
    }
}