package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 0;

    @Override   //получение списка фильмов
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override // для добавления нового фильма в список.
    public Film filmCreate(final Film film) {
        film.setId(getIdNext());
        films.put(film.getId(), film);
        return film;
    }

    @Override //для обновления данных существующего фильма.
    public Film filmUpdate(final Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override //получение фильма по id
    public Optional<Film> getFilmById(final int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override //добавление лайка
    public void addLike(final int id, final int userId) {
        films.get(id).getLikes().add(userId);
    }

    @Override // удаление лайка
    public void deleteLike(final int id, final int userId) {
        films.get(id).getLikes().remove(userId);
    }

    @Override // получение списка лучших фильмов
    public List<Film> getPopular(final int count) {
        return films.values().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getAllId() {
        return new ArrayList<>(films.keySet());
    }

    private Integer getIdNext() {
        return ++currentId;
    }
}