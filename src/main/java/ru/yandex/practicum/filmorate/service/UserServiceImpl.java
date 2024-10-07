package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).setFriend(friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).deleteFriend(friendId);
    }

    @Override
    public void addingUser(User user) {
        userStorage.addingUser(user);
    }

    @Override
    public void updateUser(User user) {
        if (userStorage.getUser((long) user.getId()) == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    @Override
    public User getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userStorage.getAllUser();
    }

    public List<Long> findCommonFriends(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friendUser = userStorage.getUser(friendId);

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> friendFriends = new HashSet<>(friendUser.getFriends());

        userFriends.retainAll(friendFriends);

        return new ArrayList<>(userFriends);
    }

    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
