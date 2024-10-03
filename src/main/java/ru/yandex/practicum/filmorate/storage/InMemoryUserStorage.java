package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addingUser(User user) {
        if (!users.containsKey(user.getId())) {

        }
    }

    @Override
    public void refreshUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }
}
