package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    List<User> getAll();

    Optional<User> getById(Integer id);

    List<User> getFriendsByUserId(Integer id);

    User addUser(User user);

    void addFriend(Integer userId, Integer friendId);

    User updateUser(User user);

    void deleteFriend(Integer userId, Integer friendId);

    Set<User> getCommonFriends(Integer id, Integer otherId);

    Boolean deleteUser(Integer id);

}