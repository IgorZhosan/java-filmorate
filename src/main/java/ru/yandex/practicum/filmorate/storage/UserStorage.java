package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    void addingUser(User user);

    void refreshUser(User user);

    void deleteUser(User user);
}
