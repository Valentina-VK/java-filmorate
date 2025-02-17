package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingMpaService {

    private final RatingMpaStorage ratingStorage;

    public RatingMpa getRatingById(Long id) {
        return ratingStorage.getRatingMpaById(id);
    }

    public List<RatingMpa> getAllRatings() {
        return ratingStorage.getRatingMpa();
    }
}