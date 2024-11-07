package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {
    Collection<User> getAllUsers();

    User userCreate(User user);

    User userUpdate(User user);

    User getUserById(int id);

    void addNewFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getAllFriends(int userId);

    List<User> getCommonFriends(int userId, int otherId);


    void deleteUser(final int id);

    Set<Film> getRecommendations(int userId);
}
