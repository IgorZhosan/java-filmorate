package ru.yandex.practicum.filmorate.service.filmService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Лайн для фильма" + filmId + "от юзера " + userId + " добавлен");
        filmStorage.getFilm(filmId).setLikes(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Лайн для фильма" + filmId + "от юзера " + userId + " удален");
        filmStorage.getFilm(filmId).deleteLike((userId));
    }

    @Override
    public List<Film> popularFilmsBasedOnLiked(int count) {
        return filmStorage.getAllFilm().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public void addingFilm(Film film) {
        log.info("Фильм добавлен");
        filmStorage.addingFilm(film);
    }

    @Override
    public void updateFilm(Film film) {
        if (!filmStorage.getAllFilm().contains(film.getId())) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        log.info("Фильм " + film + " добавлен.");
        filmStorage.updateFilm(film);

    }

    @Override
    public void deleteFilm(Long filmId) {
        log.info("Фильм " + filmId + " удалён.");
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public Film getFilm(Long filmId) {
        log.info("Фильм " + filmId + " получен.");
        return filmStorage.getFilm(filmId);
    }

    @Override
    public List<Film> getAllFilm() {
        log.info("Все фильмы возвращены");
        return filmStorage.getAllFilm();
    }

    @Override
    public void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
