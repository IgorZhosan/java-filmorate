package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {
    private final FilmService filmService;

    @PutMapping("/{filmId}/user/{userId}") // добавление лайка
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable @Positive Long filmId, @PathVariable @Positive Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/user/{userId}") // удаление лайка
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable @Positive Long filmId, @PathVariable @Positive Long userId) {
        filmService.deleteLike(filmId, userId);
    }
}
