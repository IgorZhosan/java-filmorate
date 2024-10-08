package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    public Collection<Film> getAllFilms() { //   получение списка фильмов
        log.info("Получение списка всех фильмов.");
        return filmStorage.getAllFilms();
    }

    public Film filmCreate(Film film) { // для добавления нового фильма в список.
        if (filmStorage.getFilms().containsValue(film)) {
            log.warn("Фильм с id {} уже добавлен в список.", film.getId());
            throw new DuplicatedDataException("Фильм уже добавлен.");
        }
        filmValidate(film);
        log.info("Фильм с id {} добавлен в список.", film.getId());
        return filmStorage.filmCreate(film);
    }

    public Film filmUpdate(Film film) { //для обновления данных существующего фильма.
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            log.warn("Фильм с id {} не найден.", film.getId());
            throw new NotFoundException("Фильм с id: " + film.getId() + " не найден.");
        }
        filmValidate(film);
        log.info("Фильм с id {} обновлен.", film.getId());
        return filmStorage.filmUpdate(film);
    }

    public Set<Long> addLike(Long id, Long idUser) {
        if (!filmStorage.getFilms().containsKey(id)) {
            log.warn("Ошибка при добавлении лайка. Фильм с id {} не найден.", id);
            throw new NotFoundException("Ошибка при добавлении лайка. Фильм с id: " + id + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при добавлении лайка. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка при добавлении лайка. Пользователь с id: " + idUser + " не найден.");
        }
        if (filmStorage.getFilms().get(id).getLikes().contains(idUser)) {
            log.warn("Ошибка при добавлении лайка. Пользователь уже поставил лайк.");
            throw new ValidationException("Ошибка при добавлении лайка. Пользователь уже поставил лайк.");
        }
        log.info("Пользователь с id {} добавил лайк к фильму с id {}.", idUser, id);
        filmStorage.getFilms().get(id).getLikes().add(idUser);
        return filmStorage.getFilms().get(id).getLikes();
    }

    public Set<Long> deleteLike(Long id, Long idUser) { // удаление лайка
        if (!filmStorage.getFilms().containsKey(id)) {
            log.warn("Ошибка при удалении лайка. Фильм с id {} не найден.", id);
            throw new NotFoundException("Ошибка при удалении лайка. Фильм с id: " + id + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при удалении лайка. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка при удалении лайка. Пользователь с id: " + idUser + " не найден.");
        }
        log.info("Пользователь с id {} удалил лайк к фильму с id {}.", idUser, id);
        filmStorage.getFilms().get(id).getLikes().remove(idUser);
        return filmStorage.getFilms().get(id).getLikes();
    }

    public List<Film> getPopular(Long count) {
        Collection<Film> films = filmStorage.getAllFilms(); // Получаем все фильмы
        if (films.isEmpty()) {
            log.warn("Ошибка при получении списка фильмов. Список фильмов пуст.");
            throw new NotFoundException("Ошибка при получении списка фильмов. Список фильмов пуст.");
        }
        // Сортируем фильмы по количеству лайков в порядке убывания
        return films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count) // Ограничиваем список указанным количеством
                .collect(Collectors.toList());
    }

    private void filmValidate(Film film) {
        if (film.getName() == null) {
            log.warn("Ошибка в названии фильма.");
            throw new ValidationException("Название фильма задано некорректно.");
        }
    }
}
