package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmService {

    void addLike(Film film, User userId);

    void removeLike(Film film, User user);

    List<Film> popularFilmsBasedOnLiked();


}
