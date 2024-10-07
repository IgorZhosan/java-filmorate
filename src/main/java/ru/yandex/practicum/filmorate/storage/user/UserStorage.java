package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    Collection<User> getAllUsers(); //получение списка пользователей

    User userCreate(User user); // для добавления нового пользователя в список

    User userUpdate(User user); //для обновления данных существующего пользователя

    Map<Long, User> getUsers(); //получение доступа к хранилищу с пользователями

    Set<Long> addNewFriend(Long idUser, Long idFriend); //добавление пользователя в друзья

    Set<Long> deleteFriend(Long idUser, Long idFriend); // удаление из друзей пользователя

    List<User> getAllFriends(Long idUser); // получение списка друзей пользователя

    List<User> getCommonFriends(Long idUser, Long idOther); // получение списка общих друзей с пользователем
}
