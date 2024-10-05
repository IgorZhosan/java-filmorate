package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    public List<Film> getAllFilms() {
        return filmService.getAllFilm();
    }

    @PutMapping
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        filmService.validateFilm(film);
        filmService.updateFilm(film);
        return filmService.getFilm((long) film.getId());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmService.validateFilm(film);
        filmService.addingFilm(film);
        return filmService.getFilm((long) film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return filmService.getFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.popularFilmsBasedOnLiked(count);
    }
}
