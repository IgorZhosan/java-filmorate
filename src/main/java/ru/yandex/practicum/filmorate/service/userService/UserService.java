package ru.yandex.practicum.filmorate.service.userService;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void addingUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    User getUser(Long userId);

    List<User> getAllUser();

    List<Long> findCommonFriends(Long id, Long friendId);

    void validateUser(User user);
}
