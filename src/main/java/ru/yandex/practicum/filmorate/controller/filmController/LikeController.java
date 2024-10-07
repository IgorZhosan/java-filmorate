package ru.yandex.practicum.filmorate.controller.filmController;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class LikeController {
    private final FilmService filmService;

    @PutMapping("/{id}/like/{userId}") //добавление лайка
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> addLike(@PathVariable @Positive Long id, @PathVariable("userId") @Positive Long idUser) {
        return filmService.addLike(id, idUser);
    }

    @DeleteMapping("/{id}/like/{userId}") //удаление лайка
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> deleteLike(@PathVariable @Positive Long id, @PathVariable("userId") @Positive Long idUser) {
        return filmService.deleteLike(id, idUser);
    }
}
