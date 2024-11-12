package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Film filmCreate(Film film) { // для создания фильмов
        if (film.getMpa() == null || mpaStorage.getMpaById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Invalid MPA rating provided");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Invalid Genre provided");
                }
            }
        } else {
            film.setGenres(new LinkedHashSet<>()); // Установить пустой набор, если жанры не указаны
        }
        return filmStorage.filmCreate(film);
    }

    @Override
    public Film filmUpdate(Film film) { //для обновления данных существующего фильма.
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.warn("Фильм с id {} не найден.", film.getId());
            throw new NotFoundException("Фильм с id: " + film.getId() + " не найден.");
        }
        Film filmGenre = filmValidate(film);
        Film filmNew = filmStorage.filmUpdate(filmGenre);
        log.info("Фильм с id {} обновлен.", film.getId());
        return filmNew;
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
    public Collection<Film> getPopular(final int count) { // получение списка лучших фильмов
        if (filmStorage.getAllFilms().isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        if (filmStorage.getAllFilms().size() < count) {
            return filmStorage.getPopular(filmStorage.getAllFilms().size());
        }
        return filmStorage.getPopular(count);
    }

    @Override
    @Transactional
    public void deleteFilm(final int id) {
        log.info("Начало удаления фильма с id {}", id);

        Film film = filmStorage.getFilmById(id).orElseThrow(() ->
                new NotFoundException("Фильм с id " + id + " не найден.")
        );

        log.info("Фильм найден: {}", film);

        log.info("Удаление всех связанных жанров для фильма с id {}", id);
        filmStorage.deleteGenresByFilmId(id);

        log.info("Удаление всех связанных лайков для фильма с id {}", id);
        filmStorage.deleteLikesByFilmId(id);

        log.info("Удаление фильма с id {}", id);
        filmStorage.deleteFilm(id);

        log.info("Фильм с id {} успешно удален", id);
    }

    @Override
    public List<Film> getFilmsByDirectorSorted(int directorId, String sortBy) {
        List<Film> films = filmStorage.getFilmsByDirector(directorId);

        if ("likes".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                    .collect(Collectors.toList());
        } else if ("year".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        }
        return films;
    }

    public Collection<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year) { // получение списка лучших фильмов по жанру и году
        if (filmStorage.getAllFilms().isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        if (filmStorage.getAllFilms().size() < count) {
            return filmStorage.getMostPopularFilmsByGenreAndYear(filmStorage.getAllFilms().size(), genreId, year);
        }
        return filmStorage.getMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @Override
    public Collection<Film> getMostPopularFilmsByYear(int count, int year) { // получение списка лучших фильмов по жанру и году
        if (filmStorage.getAllFilms().isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        if (filmStorage.getAllFilms().size() < count) {
            return filmStorage.getMostPopularFilmsByYear(filmStorage.getAllFilms().size(), year);
        }
        return filmStorage.getMostPopularFilmsByYear(count, year);
    }

    @Override
    public Collection<Film> getMostPopularFilmsByGenre(int count, int genreId) { // получение списка лучших фильмов по жанру и году
        if (filmStorage.getAllFilms().isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        if (filmStorage.getAllFilms().size() < count) {
            return filmStorage.getMostPopularFilmsByGenre(filmStorage.getAllFilms().size(), genreId);
        }
        return filmStorage.getMostPopularFilmsByGenre(count, genreId);
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