package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreDbRepository;

    @Override
    public List<Genre> getGenres() {
        log.info("Получение списка всех жанров.");
        return genreDbRepository.getAll();
    }

    @Override
    public Genre getGenreById(Integer id) {
        log.debug("Получение жанра с идентификатором {}", id);
        return genreDbRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Жанра с заданным идентификатором не существует"));
    }
}