package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.RatingMpaDto;
import ru.yandex.practicum.filmorate.model.RatingMpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingMpaMapper {

    public static RatingMpaDto toDto(RatingMpa mpa) {
        RatingMpaDto dto = new RatingMpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}