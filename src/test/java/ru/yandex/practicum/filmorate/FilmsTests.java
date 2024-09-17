package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmsTests {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldGetAllFilms() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.getAllFilms();
        });
        assertEquals("Список фильмов пуст", exception.getMessage());
    }

    @Test
    void shouldAddFilm() {
        Film film = new Film("Film Name", "Description", LocalDate.of(2000, 1, 1), 120);

        Film addedFilm = filmController.addFilm(film);  // Обновленный метод

        assertEquals(film, addedFilm);
        assertEquals(1, filmController.getAllFilms().size());
    }
}
