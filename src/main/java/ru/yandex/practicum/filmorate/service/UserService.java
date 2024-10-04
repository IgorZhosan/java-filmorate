package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<User> getAllFriend(User friendsUser);
}
