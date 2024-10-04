package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public void addLike(Film film, User userId) {
        filmStorage.getFilm(film).setLikes((long) userId.getId());
    }

    @Override
    public void removeLike(Film film, User userId) {
        filmStorage.getFilm(film).deleteLike((long) userId.getId());
    }

    @Override
    public List<Film> popularFilmsBasedOnLiked() {
        return List.of();
    }
}
