package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.service.film.FilmService;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping //   получение списка фильмов
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping() // для добавления нового фильма в список.
    @ResponseStatus(HttpStatus.CREATED)
    public Film filmCreate(@Valid @RequestBody Film film) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        return filmService.filmCreate(film);
    }

    @PutMapping() //для обновления данных существующего фильма.
    public Film filmUpdate(@Valid @RequestBody Film film) {
        return filmService.filmUpdate(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @Positive @RequestBody int id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}") //добавление лайка
    public void addLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") //удаление лайка
    public void deleteLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        if (genreId == null && year == null) {
            return filmService.getPopular(count);
        } else if (genreId == null) {
            return filmService.getMostPopularFilmsByYear(count, year);
        } else if (year == null) {
            return filmService.getMostPopularFilmsByGenre(count, genreId);
        }
        return filmService.getMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> getSearchedFilms(
            @RequestParam(value = "query", required = true) String query,
            @RequestParam(value = "by", required = true) String by) {
        Set<SearchType> searchTypes = SearchType.toEnum(by);
        return filmService.getSearchedFilms(query, searchTypes);
    }
}