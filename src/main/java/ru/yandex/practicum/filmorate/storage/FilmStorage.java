package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(Film film);

    Film update(Film newFilm);

    Film getFilmById(long id);

    Film userLikesFilm(Long id, Long userId);

    Film deleteLikesFilm(Long id, Long userId);

    List<Film> getPopularFilms(int count);
}