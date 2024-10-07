package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmStorage.getFilm(filmId).setLikes(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
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
        filmStorage.addingFilm(film);
    }

    @Override
    public void updateFilm(Film film) {
        if (filmStorage.getFilm((long) film.getId()) == null) {
            throw new RuntimeException("фильм не найден");
        }
        filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public Film getFilm(Long filmId) {
        return filmStorage.getFilm(filmId);
    }

    @Override
    public List<Film> getAllFilm() {
        return filmStorage.getAllFilm();
    }

    @Override
    public void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
