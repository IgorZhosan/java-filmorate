package ru.yandex.practicum.filmorate.service.userService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Друг " + friendId + " пользователя " + userId + "добавлен.");
        userStorage.getUser(userId).setFriend(friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Друг " + friendId + " пользователя " + userId + " удален.");
        userStorage.getUser(userId).deleteFriend(friendId);
    }

    @Override
    public void addingUser(User user) {
        log.info("Пользователь " + user + "добавлен.");
        userStorage.addingUser(user);
    }

    @Override
    public void updateUser(User user) {
        if (userStorage.getUser((long) user.getId()) == null) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        log.info("Пользователь " + user + " удалён.");
        userStorage.deleteUser(user);
    }

    @Override
    public User getUser(Long userId) {
        log.info("Пользователь " + userId + " возвращен.");
        return userStorage.getUser(userId);
    }

    @Override
    public List<User> getAllUser() {
        log.info("Все пользователи возвращены");
        return userStorage.getAllUser();
    }

    public List<Long> findCommonFriends(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friendUser = userStorage.getUser(friendId);

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> friendFriends = new HashSet<>(friendUser.getFriends());

        userFriends.retainAll(friendFriends);

        log.info("Список друга друзей возвращен");
        return new ArrayList<>(userFriends);
    }

    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
