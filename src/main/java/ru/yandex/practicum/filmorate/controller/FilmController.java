package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping //   получение списка фильмов
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getAllFilms() {
        return List.copyOf(filmService.getAllFilms());
    }

    @PostMapping() // для добавления нового фильма в список.
    @ResponseStatus(HttpStatus.CREATED)
    public Film filmCreate(@Valid @RequestBody Film film) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        return filmService.filmCreate(film);
    }

    @PutMapping() //для обновления данных существующего фильма.
    @ResponseStatus(HttpStatus.OK)
    public Film filmUpdate(@Valid @RequestBody Film film) {
        return filmService.filmUpdate(film);
    }

    @GetMapping("/popular")  // получение списка лучших фильмов
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive Long count) {
        return filmService.getPopular(count);
    }
}

