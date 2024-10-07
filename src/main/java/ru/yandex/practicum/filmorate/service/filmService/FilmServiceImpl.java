package ru.yandex.practicum.filmorate.service.filmService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.warn("Ошибка: фильм с id={} не найден.", filmId);
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
        if (userStorage.getUser(userId) == null) {
            log.warn("Ошибка: пользователь с id={} не найден.", userId);
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }
        film.getLikes().add(userId);
        log.info("Пользователь с id={} добавил лайк к фильму с id={}.", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.warn("Ошибка: фильм с id={} не найден.", filmId);
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
        if (userStorage.getUser(userId) == null) {
            log.warn("Ошибка: пользователь с id={} не найден.", userId);
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }
        if (!film.getLikes().contains(userId)) {
            log.warn("Ошибка: лайк от пользователя с id={} не найден.", userId);
            throw new ValidationException("Пользователь с id: " + userId + " не ставил лайк фильму с id: " + filmId);
        }
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк к фильму с id={}.", userId, filmId);
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
        validateFilm(film);
        log.info("Фильм добавлен");
        filmStorage.addingFilm(film);
    }

    @Override
    public void updateFilm(Film film) {
        if (filmStorage.getFilm((long) film.getId()) == null) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        validateFilm(film);
        log.info("Фильм с id={} обновлен.", film.getId());
        filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilm(Long filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.warn("Ошибка: фильм с id={} не найден.", filmId);
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
        log.info("Фильм с id={} удалён.", filmId);
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public Film getFilm(Long filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.warn("Фильм с id={} не найден.", filmId);
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
        log.info("Фильм с id={} получен.", filmId);
        return film;
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
