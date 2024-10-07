package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private int userIdSequence = 1;

    @Override
    public void addingUser(User user) {
        if (!users.containsKey(user.getId())) {
            user.setId(userIdSequence++);
            users.put((long) user.getId(), user);
        } else throw new ValidationException("Такой пользователь уже есть");
    }

    @Override
    public void updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put((long) user.getId(), user);
        } else throw new ValidationException("Нельзя обновить пользователя, которого нет");
    }

    @Override
    public void deleteUser(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
        } else throw new ValidationException("Нельзя удалить того, кого нет");
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else throw new ValidationException("Такого пользователя нет");
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(users.values());
    }
}
