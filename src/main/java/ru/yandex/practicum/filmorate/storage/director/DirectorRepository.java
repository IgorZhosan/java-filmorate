package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    List<Director> getAll();

    Optional<Director> getDirectorById(Integer id);

    Director addDirector(Director director);

    void deleteDirector(Integer id);

    Director updateDirector(Director director);
}