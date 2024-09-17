package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int filmIdSequence = 1;

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());  // Возвращаем пустой список вместо ошибки
    }

    @PutMapping
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(filmIdSequence++);  // Присваиваем новый id при добавлении
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
