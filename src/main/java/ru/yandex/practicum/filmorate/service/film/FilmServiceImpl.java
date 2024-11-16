package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmDbRepository;
    private final MpaStorage mpaDbRepository;
    private final GenreStorage genreDbRepository;
    private final UserStorage userDbRepository;
    private final DirectorService directorService;
    private final EventService eventService;

    @Override
    public List<Film> getFilms() {
        return filmDbRepository.getAll();
    }

    @Override
    public Film getFilmById(Integer id) {
        return filmDbRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
    }

    @Override
    public Film addFilm(Film film) {
        mpaDbRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new ValidationException("Ошибка! Рейтинга с заданным идентификатором не существует"));
        checkGenres(film);
        return filmDbRepository.addFilm(film);
    }

    @Override
    public Boolean deleteFilm(Integer id) {
        filmDbRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
        return filmDbRepository.deleteFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Изменение параметров фильма с идентификатором {}", film.getId());
        filmDbRepository.getById(film.getId())
                .orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
        mpaDbRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Ошибка! Рейтинга с заданным идентификатором не существует"));
        checkGenres(film);
        return filmDbRepository.updateFilm(film);
    }

    @Override
    public void addUserLike(Integer filmId, Integer userId) {
        filmDbRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
        userDbRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
        filmDbRepository.addUserLike(filmId, userId);
        eventService.register(userId, Operation.ADD, EventType.LIKE, filmId);
    }

    @Override
    public void deleteUserLike(Integer filmId, Integer userId) {
        filmDbRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
        userDbRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
        eventService.register(userId, Operation.REMOVE, EventType.LIKE, filmId);
        filmDbRepository.deleteUserLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (year == null && genreId == null) {
            return filmDbRepository.getPopularFilms(count);
        } else if (year != null && genreId == null) {
            return filmDbRepository.getPopularFilmsWithYear(count, year);
        } else if (year == null && genreId != null) {
            return filmDbRepository.getPopularFilmsWithGenre(count, genreId);
        } else {
            return filmDbRepository.getPopularFilmsWithGenreAndYear(count, year, genreId);
        }
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        if (!"year".equals(sortBy) && !"likes".equals(sortBy)) {
            log.error("Переданы некорректные параметры запроса.");
            throw new NotFoundException("Ошибка! Параметры запроса некорректны.");
        }
        directorService.getDirectorById(directorId);
        return filmDbRepository.getFilmsByDirector(directorId, sortBy);
    }

    @Override
    public Set<Film> getCommonFilms(Integer userId, Integer friendId) {
        Set<Film> userFilms = filmDbRepository.getLikeFilmsByUserId(userId);
        userFilms.retainAll(filmDbRepository.getLikeFilmsByUserId(friendId));
        return userFilms;
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        Set<String> validByValues = new HashSet<>(Arrays.asList("director", "title", "director,title", "title,director"));
        if (query == null || by == null || !validByValues.contains(by)) {
            log.error("Переданы некорректные параметры запроса.");
            throw new IllegalArgumentException("Ошибка! Параметры запроса некорректны.");
        }
        return filmDbRepository.searchFilm(query, by);
    }

    private void checkGenres(Film film) {
        if (film.getGenres() != null) {
            Set<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<Genre> genres = genreDbRepository.getByIds(genreIds);
            if (genreIds.size() != genres.size()) {
                throw new ValidationException("Ошибка! Жанра с заданным идентификатором не существует");
            }
        }
    }
}