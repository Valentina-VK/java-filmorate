package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingMpaStorage {
    List<RatingMpa> getRatingMpa();

    RatingMpa getRatingMpaById(long id);
}