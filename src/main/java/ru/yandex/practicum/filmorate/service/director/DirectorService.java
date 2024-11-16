package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getAll();

    Director getDirectorById(Integer id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer id);
}
