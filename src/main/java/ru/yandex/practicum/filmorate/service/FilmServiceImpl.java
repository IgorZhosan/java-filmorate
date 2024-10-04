package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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
    public void addLike(Long filmId, Long userId) {
        filmStorage.getFilm(filmId).setLikes(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        filmStorage.getFilm(filmId).deleteLike((userId));
    }

    @Override
    public List<Film> popularFilmsBasedOnLiked() {
        return List.of();
    }
}
