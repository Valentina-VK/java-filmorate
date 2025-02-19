package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        return FilmMapper.toDto(filmStorage.getFilms());
    }

    public FilmDto getFilmById(long id) {
        return FilmMapper.toDto(filmStorage.getFilmById(id));
    }

    public FilmDto createFilm(NewFilmRequest film) {
        return FilmMapper.toDto(filmStorage.create(FilmMapper.toEntity(film)));
    }

    public FilmDto updateFilm(UpdateFilmRequest newFilm) {
        filmStorage.getFilmById(newFilm.getId());
        return FilmMapper.toDto(filmStorage.update(FilmMapper.toEntity(newFilm)));
    }

    public FilmDto addLikeToFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        return FilmMapper.toDto(filmStorage.userLikesFilm(filmId, userId));
    }

    public FilmDto removeLikeFromFilm(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователь не найден, id: " + userId);
        return FilmMapper.toDto(filmStorage.deleteLikesFilm(filmId, userId));
    }

    public List<FilmDto> getPopularFilms(int count) {
        return FilmMapper.toDto(filmStorage.getPopularFilms(count));
    }
}