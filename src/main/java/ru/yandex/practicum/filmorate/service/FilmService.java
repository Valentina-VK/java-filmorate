package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<FilmDto> getFilms() {
        return filmStorage.getFilms().stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public FilmDto getFilmById(long id) {
        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(id));
    }

    public FilmDto createFilm(NewFilmRequest film) {
        return FilmMapper.mapToFilmDto(filmStorage.create(FilmMapper.mapToFilm(film)));
    }

    public FilmDto updateFilm(UpdateFilmRequest newFilm) {
        Film film = FilmMapper.updateFilmFields(filmStorage.getFilmById(newFilm.getId()), newFilm);
        return FilmMapper.mapToFilmDto(filmStorage.update(film));
    }

    public FilmDto addLikeToFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        return FilmMapper.mapToFilmDto(filmStorage.userLikesFilm(filmId, userId));
    }

    public FilmDto removeLikeFromFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        return FilmMapper.mapToFilmDto(filmStorage.deleteLikesFilm(filmId, userId));
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getNumberOfLikes).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }
}