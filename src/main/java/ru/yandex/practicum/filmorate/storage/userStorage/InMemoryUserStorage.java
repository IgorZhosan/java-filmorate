package ru.yandex.practicum.filmorate.storage.userStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private int userIdSequence = 1;

    @Override
    public void addingUser(User user) {
        if (!users.containsKey((long) user.getId())) {
            user.setId(userIdSequence++);
            log.info("Пользователь " + user + " добавлен.");
            users.put((long) user.getId(), user);
        } else {
            log.warn("Такой пользователь уже есть.");
            throw new DuplicateException("Такой пользователь уже есть.");
        }
    }

    @Override
    public void updateUser(User user) {
        if (users.containsKey((long) user.getId())) {
            log.info("Пользователь " + user + " добавлен.");
            users.put((long) user.getId(), user);
        } else {
            log.warn("Нельзя обновить пользователя, которого нет");
            throw new NotFoundException("Нельзя обновить пользователя, которого нет");
        }
    }

    @Override
    public void deleteUser(User user) {
        if (users.containsKey((long) user.getId())) {
            log.info("Пользователь " + user + " удалён");
            users.remove((long) user.getId());
        } else {
            log.warn("Нельзя удалить того, кого нет");
            throw new NotFoundException("Нельзя удалить того, кого нет");
        }
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            log.info("Пользователь " + userId + "возвращен.");
            return users.get(userId);
        } else {
            log.warn("Такого пользователя нет");
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public List<User> getAllUser() {
        log.info("Все пользователи возвращены");
        return new ArrayList<>(users.values());
    }
}
