package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmServiceImpl(@Qualifier("jdbcFilmStorage") FilmStorage filmStorage, UserService userService, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Film> getAllFilms() { //   получение списка фильмов
        log.info("Получение списка всех фильмов.");
        if (filmStorage.getAllFilms().isEmpty()) {
            return new ArrayList<>();
        }
        return filmStorage.getAllFilms();
    }

    @Override
    public Film filmCreate(final Film film) { // для добавления нового фильма в список.
        if (filmStorage.getAllFilms().contains(film)) {
            log.warn("Фильм с id {} уже добавлен в список.", film.getId());
            throw new DuplicatedDataException("Фильм уже добавлен.");
        }
        Film filmGenre = filmValidate(film);
        Film film1 = filmStorage.filmCreate(filmGenre);
        log.info("Фильм с id {} добавлен в список.", film.getId());
        return film1;
    }

    @Override
    public Film filmUpdate(final Film film) { //для обновления данных существующего фильма.
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.warn("Фильм с id {} не найден.", film.getId());
            throw new NotFoundException("Фильм с id: " + film.getId() + " не найден.");
        }
        Film filmGenre = filmValidate(film);
        Film film1 = filmStorage.filmUpdate(filmGenre);
        log.info("Фильм с id {} обновлен.", film.getId());
        return film1;
    }

    @Override
    public Film getFilmById(final int id) {
        log.info("Получение фильма по id.");
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + id + " не существует."));
    }

    @Override
    public void addLike(final int id, final int userId) { //добавление лайка
        getFilmById(id);
        userService.getUserById(userId);
        if (filmStorage.getFilmById(id).get().getLikes().contains(userId)) {
            log.warn("Ошибка при добавлении лайка. Пользователь уже поставил лайк.");
            throw new ValidationException("Ошибка при добавлении лайка. Пользователь уже поставил лайк.");
        }
        log.info("Пользователь с id {} добавил лайк к фильму с id {}.", userId, id);
        filmStorage.addLike(id, userId);
    }

    @Override
    public void deleteLike(final int id, final int userId) { // удаление лайка
        getFilmById(id);
        userService.getUserById(userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}.", userId, id);
        filmStorage.deleteLike(id, userId);

    }

    @Override
    public List<Film> getPopular(final int count) { // получение списка лучших фильмов
        if (filmStorage.getAllFilms().isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        if (filmStorage.getAllFilms().size() < count) {
            return filmStorage.getPopular(filmStorage.getAllFilms().size());
        }
        return filmStorage.getPopular(count);
    }

    private Film filmValidate(final Film film) {
        if (Objects.nonNull(film.getMpa())) {
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new ValidationException("Рейтинг введён некорректно."))
            );
        }
        if (Objects.nonNull(film.getGenres())) {
            List<Integer> idGenres = film.getGenres().stream().map(Genre::getId).toList();
            LinkedHashSet<Genre> genres = genreStorage.getGenresList(idGenres).stream()
                    .sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
            if (film.getGenres().size() == genres.size()) {
                film.getGenres().clear();
                film.setGenres(genres);
            } else {
                log.warn("Жанр введен некорректно.");
                throw new ValidationException("Жанр введен некорректно.");
            }
        }
        return film;
    }
}
