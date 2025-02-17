package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.RatingMpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, RatingMpaDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, RatingMpaRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmsDbStorage;
    public static final Film testFilm = new Film();

    @BeforeAll
    public static void setTestFilm() {
        testFilm.setName("TestFilm");
        testFilm.setDescription("Description for TestFilm");
        testFilm.setReleaseDate(LocalDate.parse("1919-09-19"));
        testFilm.setDuration(100);
        RatingMpa mpa = new RatingMpa(1,"");
        testFilm.setRatingMpa(mpa);
        Genre genreOne = new Genre(1,"");
        Genre genreTwo = new Genre(3,"");
        testFilm.setGenres(Set.of(genreOne, genreTwo));
    }

    @Test
    public void testGetAllFilms() {
        List<Film> allFilms = filmsDbStorage.getFilms();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms).hasSize(5);
    }

    @Test
    public void testGetFilmById() {
        Film film = filmsDbStorage.getFilmById(5);

        assertThat(film).isNotNull();
        assertThat(film.getName()).isEqualTo("Фильм 5");
    }

    @Test
    public void testCreateFilm() {
        Film addedFilm = filmsDbStorage.create(testFilm);

        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isPositive();
        assertThat(addedFilm.getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testUpdateFilm() {
        testFilm.setId(5L);
        Film updatedFilm = filmsDbStorage.update(testFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(5L);
        assertThat(updatedFilm.getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testUserLikesFilm() {
        Film likedFilm = filmsDbStorage.userLikesFilm(5L, 1L);

        assertThat(likedFilm).isNotNull();
        assertThat(likedFilm.getId()).isEqualTo(5L);
        assertThat(likedFilm.getLikes()).hasSize(1);
        assertThat(likedFilm.getLikes()).contains(1L);
    }

    @Test
    public void testDeleteLikesFilm() {
        Film likedFilm = filmsDbStorage.deleteLikesFilm(1L, 1L);

        assertThat(likedFilm).isNotNull();
        assertThat(likedFilm.getId()).isEqualTo(1L);
        assertThat(likedFilm.getLikes()).hasSize(2);
        assertThat(likedFilm.getLikes()).contains(2L, 3L);
    }
}