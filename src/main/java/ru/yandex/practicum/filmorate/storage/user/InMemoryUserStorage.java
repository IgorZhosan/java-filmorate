package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override //получение списка пользователей.
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override // для добавления нового пользователя в список.
    public User userCreate(User user) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        user.setId(getIdNext());
        users.put(user.getId(), user);
        return user;
    }

    @Override //для обновления данных существующего пользователя.
    public User userUpdate(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    private long getIdNext() {
        return ++currentId;
    }
}
