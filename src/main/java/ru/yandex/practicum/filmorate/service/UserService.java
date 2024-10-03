package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

public interface UserService {

    void addFriend(User friendUser);

    void deleteFriend(User friendUser);

    void getAllFriend(User friendsUser);
}
