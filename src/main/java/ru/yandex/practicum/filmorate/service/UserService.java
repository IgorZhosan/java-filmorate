package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserStorage userStorage;

    public Collection<User> getAllUsers() { //получение списка пользователей.
        log.info("Получение списка всех пользователей");
        return userStorage.getAllUsers();
    }

    public User userCreate(User user) { // для добавления нового пользователя в список.
        if (userStorage.getUsers().containsValue(user)) {
            log.warn("Пользователь с id {} уже добавлен в список.", user.getId());
            throw new DuplicatedDataException("Этот пользователь уже существует.");
        }
        userValidate(user);
        log.info("Пользователь с id {} добавлен.", user.getId());
        return userStorage.userCreate(user);
    }

    public User userUpdate(User user) { //для обновления данных существующего пользователя.
        if (user.getId() == null || !userStorage.getUsers().containsKey(user.getId())) {
            log.warn("Пользователь с id {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id: " + user.getId() + " не найден.");
        }
        userValidate(user);
        log.info("Пользователь с id {} обновлен.", user.getId());
        return userStorage.userUpdate(user);
    }

    public Set<Long> addNewFriend(Long idUser, Long idFriend) { //добавление пользователя в друзья
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при добавлении в друзья. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка при добавлении в друзья. Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idFriend)) {
            log.warn("Ошибка при добавлении в друзья. Пользователь с id {} не найден.", idFriend);
            throw new NotFoundException("Ошибка при добавлении в друзья. Пользователь с id: " + idFriend + " не найден.");
        }
        if (userStorage.getUsers().get(idUser).getFriends().contains(idFriend) || userStorage.getUsers().get(idFriend).getFriends().contains(idUser)) {
            log.warn("Ошибка при добавлении в друзья. Пользователь уже в друзьях.");
            throw new ValidationException("Ошибка при добавлении в друзья. Пользователь уже в друзьях.");
        }
        if (idUser.equals(idFriend)) {
            log.warn("Ошибка при добавлении в друзья. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при добавлении в друзья. Id пользователей совпадают.");
        }
        log.info("Пользователь с id {} добавлен в друзья к пользователю с id {}.", idFriend, idUser);
        return userStorage.addNewFriend(idUser, idFriend);
    }

    public Set<Long> deleteFriend(Long idUser, Long idFriend) { // удаление из друзей пользователя
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при удалении пользователя из друзей. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка при удалении пользователя из друзей. Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idFriend)) {
            log.warn("Ошибка при удалении пользователя из друзей. Пользователь с id {} не найден.", idFriend);
            throw new NotFoundException("Ошибка при удалении пользователя из друзей. Пользователь с id: " + idFriend + " не найден.");
        }
        if (idUser.equals(idFriend)) {
            log.warn("Ошибка при удалении пользователя из друзей. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при удалении пользователя из друзей. Id пользователей совпадают.");
        }
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", idFriend, idUser);
        return userStorage.deleteFriend(idUser, idFriend);
    }

    public List<User> getAllFriends(Long idUser) { // получение списка друзей пользователя
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка получения списка друзей пользователя. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка получения списка друзей пользователя. Пользователь с id: " + idUser + " не найден.");
        }
        log.info("Получение списка друзей пользователя с id {}.", idUser);
        return userStorage.getAllFriends(idUser);
    }

    public List<User> getCommonFriends(Long idUser, Long idOther) { // получение списка общих друзей с пользователем
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при получении списка общих друзей. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Ошибка при получении списка общих друзей. Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idOther)) {
            log.warn("Ошибка при получении списка общих друзей. Пользователь с id {} не найден.", idOther);
            throw new NotFoundException("Ошибка при получении списка общих друзей. Пользователь с id: " + idOther + " не найден.");
        }
        if (idUser.equals(idOther)) {
            log.warn("Ошибка при получении списка общих друзей. Id пользователей совпадают.");
            throw new ValidationException("Ошибка при получении списка общих друзей. Id пользователей совпадают.");
        }
        log.info("Получение списка общих друзей у пользователя с id {} и пользователя с id {}.", idOther, idUser);
        return userStorage.getCommonFriends(idUser, idOther);
    }

    private void userValidate(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка в написании даты рождения.");
            throw new ValidationException("Дата рождения не может быть задана в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка в написании логина.");
            throw new ValidationException("Логин не должен содержать пробелы.");
        }
    }
}
