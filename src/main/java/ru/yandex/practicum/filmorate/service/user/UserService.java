package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    List<User> addFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer id);

    User updateUser(User user);

    void deleteFriend(Integer userId, Integer friendId);

    Set<User> getCommonFriends(Integer id, Integer otherId);

    Boolean deleteUser(Integer id);

    Set<Film> getRecommendations(Integer id);
}
