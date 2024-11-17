package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("jdbcUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> getAllUsers() { //получение списка пользователей.
        log.info("Получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User userCreate(final User user) { // для добавления нового пользователя в список.
        if (userStorage.getAllUsers().contains(user)) {
            log.warn("Пользователь с id {} уже добавлен в список.", user.getId());
            throw new DuplicatedDataException("Этот пользователь уже существует.");
        }
        userValidate(user);
        User userNew = userStorage.userCreate(user);
        log.info("Пользователь с id {} добавлен.", user.getId());
        return userNew;
    }

    @Override
    public User userUpdate(final User user) { //для обновления данных существующего пользователя.
        if (user.getId() == null || userStorage.getUserById(user.getId()).isEmpty()) {
            log.warn("Пользователь с id {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id: " + user.getId() + " не найден.");
        }
        userValidate(user);
        User userNew = userStorage.userUpdate(user);
        log.info("Пользователь с id {} обновлен.", user.getId());
        return userNew;
    }

    @Override
    public User getUserById(final int id) { // получение пользователя по id
        log.info("Получение пользователя по id.");
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден."));
    }

    @Override
    public void addNewFriend(final int userId, final int friendId) { //добавление пользователя в друзья
        getUserById(userId);
        getUserById(friendId);
        if (userId == friendId) {
            log.warn("Ошибка при добавлении в друзья. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при добавлении в друзья. Id пользователей совпадают.");
        }
        userStorage.addNewFriend(userId, friendId);
        log.info("Пользователь с id {} добавлен в друзья к пользователю с id {}.", friendId, userId);
    }

    @Override
    public void deleteFriend(final int userId, final int friendId) { // удаление из друзей пользователя
        getUserById(userId);
        getUserById(friendId);
        if (userId == friendId) {
            log.warn("Ошибка при удалении пользователя из друзей. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при удалении пользователя из друзей. Id пользователей совпадают.");
        }
        userStorage.deleteFriend(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", friendId, userId);
    }

    @Override
    public List<User> getAllFriends(final int userId) { // получение списка друзей пользователя
        getUserById(userId);
        List<User> listFriends = userStorage.getAllFriends(userId);
        log.info("Получение списка друзей пользователя с id {}.", userId);
        return listFriends;
    }

    @Override
    public List<User> getCommonFriends(final int userId, final int otherId) { // получение списка общих друзей с пользователем
        getUserById(userId);
        getUserById(otherId);
        if (userId == otherId) {
            log.warn("Ошибка при получении списка общих друзей. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при получении списка общих друзей. Id пользователей совпадают.");
        }
        List<User> listFriends = userStorage.getCommonFriends(userId, otherId);
        log.info("Получение списка общих друзей у пользователя с id {} и пользователя с id {}.", otherId, userId);
        return listFriends;
    }

    private void userValidate(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
