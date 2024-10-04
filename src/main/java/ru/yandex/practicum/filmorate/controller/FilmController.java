package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }


    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilm();
    }

    @PutMapping
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        filmStorage.updateFilm(film);
        return filmStorage.getFilm((long) film.getId());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        filmStorage.addingFilm(film);
        return filmStorage.getFilm(1L);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return filmStorage.getFilm(id);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
