package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Collection<User> getAllUsers(); //получение списка пользователей

    User userCreate(User user); // для добавления нового пользователя в список

    User userUpdate(User user); //для обновления данных существующего пользователя

    Map<Long, User> getUsers(); //получение доступа к хранилищу с пользователями
}
