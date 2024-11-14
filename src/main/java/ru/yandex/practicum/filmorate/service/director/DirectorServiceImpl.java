package ru.yandex.practicum.filmorate.service.director;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден."));
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director createDirector(Director director) {
        if (director.getName().isBlank() || director.getName().isEmpty()) {
            throw new ValidationException("Имя режиссера не может быть пустым.");
        }
        return directorStorage.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() == null || directorStorage.getDirectorById(director.getId()).isEmpty()) {
            throw new NotFoundException("Режиссер с id " + director.getId() + " не найден.");
        }
        return directorStorage.updateDirector(director);
    }

    @Override
    public void deleteDirector(int id) {
        if (directorStorage.getDirectorById(id).isEmpty()) {
            throw new NotFoundException("Режиссер с id " + id + " не найден.");
        }
        directorStorage.deleteDirector(id);
    }
}