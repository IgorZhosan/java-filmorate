package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.filmService.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        return filmService.getAllFilm();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        filmService.validateFilm(film);
        filmService.updateFilm(film);
        return filmService.getFilm((long) film.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.validateFilm(film);
        filmService.addingFilm(film);
        return filmService.getFilm((long) film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film setLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return filmService.getFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.popularFilmsBasedOnLiked(count);
    }
}
