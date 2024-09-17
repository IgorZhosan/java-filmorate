package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmsTests {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = filmController.getAllFilms();
        assertEquals(0, films.size(), "Список фильмов должен быть пуст");
    }


    @Test
    void shouldAddFilm() {
        Film film = new Film("Film Name", "Description", LocalDate.of(2000, 1, 1), 120);

        Film addedFilm = filmController.addFilm(film);  // Обновленный метод

        assertEquals(film, addedFilm);
        assertEquals(1, filmController.getAllFilms().size());
    }
}
