package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorDbRepository;

    @Override
    public List<Director> getAll() {
        log.info("Получение списка всех директоров.");
        return directorDbRepository.getAll();
    }

    @Override
    public Director getDirectorById(Integer id) {
        return directorDbRepository.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Директора с заданным идентификатором не существует"));
    }

    @Override
    public Director addDirector(Director director) {
        log.info("Директор с id {} добавлен.", director.getId());
        return directorDbRepository.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        log.debug("Изменение параметров директора с идентификатором {}", director.getId());
        directorDbRepository.getDirectorById(director.getId())
                .orElseThrow(() -> new NotFoundException("Ошибка! Директора с заданным идентификатором не существует"));
        return directorDbRepository.updateDirector(director);
    }

    @Override
    public void deleteDirector(Integer id) {
        getDirectorById(id);
        log.info("Директор с id {} удалён.", id);
        directorDbRepository.deleteDirector(id);
    }
}