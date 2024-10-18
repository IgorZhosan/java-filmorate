package ru.yandex.practicum.filmorate.service.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public MpaServiceImpl(@Qualifier("jdbcMpaStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Mpa getMpaById(final int id) {
        log.info("Получение рейтинга по id.");
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id: " + id + " не существует."));
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Получение списка всех рейтингов.");
        return mpaStorage.getAllMpa();
    }
}
