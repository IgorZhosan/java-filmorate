package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> getAllUsers();

    User userCreate(User user);

    User userUpdate(User user);

    User getUserById(int id);

    void addNewFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getAllFriends(int userId);

    List<User> getCommonFriends(int userId, int otherId);
}
