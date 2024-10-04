package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void addingUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    User getUser(User user);

    List<User> getAllUser();
}
