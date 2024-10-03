package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    void addLike();

    void removeLike();

    List<Film> popularFilmsBasedOnLiked();


}
