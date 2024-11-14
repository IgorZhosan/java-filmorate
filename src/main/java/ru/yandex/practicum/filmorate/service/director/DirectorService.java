package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director getDirectorById(int id);

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}