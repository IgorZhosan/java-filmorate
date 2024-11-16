package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    List<Mpa> getMpa();

    Mpa getRatingById(Integer id);

    Mpa addRating(Mpa rating);
}
