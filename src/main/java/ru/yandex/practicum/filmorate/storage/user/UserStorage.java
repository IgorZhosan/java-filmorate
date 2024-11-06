package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers(); //получение списка пользователей

    User userCreate(User user); // для добавления нового пользователя в список

    User userUpdate(User user); //для обновления данных существующего пользователя

    Optional<User> getUserById(int id); //получение пользователя по id

    void addNewFriend(int userId, int friendId); //добавление пользователя в друзья

    void deleteFriend(int userId, int friendId); // удаление из друзей пользователя

    List<User> getAllFriends(int userId); // получение списка друзей пользователя

    void deleteUser(final int userId); //удаление юзера по id

    List<User> getCommonFriends(int userId, int otherId); // получение списка общих друзей с пользователем
}
