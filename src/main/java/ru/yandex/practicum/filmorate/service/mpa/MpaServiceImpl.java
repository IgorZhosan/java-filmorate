package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaDbRepository;

    @Override
    public List<Mpa> getMpa() {
        log.info("Получение списка всех рейтингов.");
        return mpaDbRepository.getAll();
    }

    @Override
    public Mpa getRatingById(Integer id) {
        log.debug("Получение рейтинга с идентификатором {}", id);
        return mpaDbRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Рейтинга с заданным идентификатором не существует"));
    }

    @Override
    public Mpa addRating(Mpa rating) {
        log.info("Добавление нового рейтинга: {}", rating);
        return mpaDbRepository.addRating(rating);
    }
}