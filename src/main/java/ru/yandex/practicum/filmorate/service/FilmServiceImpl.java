package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService{
    @Override
    public void addLike() {

    }

    @Override
    public void removeLike() {

    }

    @Override
    public List<Film> popularFilmsBasedOnLiked() {
        return List.of();
    }
}
