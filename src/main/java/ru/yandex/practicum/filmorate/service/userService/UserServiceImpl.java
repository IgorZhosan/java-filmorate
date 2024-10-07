package ru.yandex.practicum.filmorate.service.userService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь с id={} уже является другом пользователя с id={}", friendId, userId);
            throw new ValidationException("Пользователь уже в друзьях");
        }

        if (userId.equals(friendId)) {
            log.warn("Ошибка: пользователь не может добавить сам себя в друзья.");
            throw new ValidationException("Пользователь не может добавить сам себя в друзья.");
        }

        log.info("Друг {} добавлен пользователю {}.", friendId, userId);
        user.setFriend(friendId);
        friend.setFriend(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (!user.getFriends().contains(friendId)) {
            log.warn("Пользователь с id={} не является другом пользователя с id={}", friendId, userId);
            throw new NotFoundException("Пользователь с id=" + friendId + " не является другом пользователя с id=" + userId);
        }

        log.info("Друг {} пользователя {} удален.", friendId, userId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
    }

    @Override
    public void addingUser(User user) {
        if (userStorage.getUser((long) user.getId()) != null) {
            log.warn("Такой пользователь уже есть.");
            throw new ValidationException("Такой пользователь уже существует.");
        }
        log.info("Пользователь {} добавлен.", user);
        userStorage.addingUser(user);
    }

    @Override
    public void updateUser(User user) {
        if (userStorage.getUser((long) user.getId()) == null) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} обновлен.", user);
        userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        if (userStorage.getUser((long) user.getId()) == null) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь {} удалён.", user);
        userStorage.deleteUser(user);
    }

    @Override
    public User getUser(Long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        log.info("Пользователь {} возвращен.", userId);
        return user;
    }

    @Override
    public List<User> getAllUser() {
        log.info("Все пользователи возвращены");
        return userStorage.getAllUser();
    }

    public List<Long> findCommonFriends(Long id, Long friendId) {
        User user = getUser(id);
        User friendUser = getUser(friendId);

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> friendFriends = new HashSet<>(friendUser.getFriends());

        userFriends.retainAll(friendFriends);

        log.info("Список общих друзей между пользователями с id={} и id={} возвращен.", id, friendId);
        return new ArrayList<>(userFriends);
    }

    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка в написании логина.");
            throw new ValidationException("Логин не должен содержать пробелы.");
        }
    }
}
