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
import java.util.stream.Collectors;

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
    //для обновления данных существующего пользователя.
    public User userUpdate(User user) {
        if (user.getId() == null || !userStorage.getUsers().containsKey(user.getId())) {
            log.warn("Пользователь с id {} не найден.", user.getId());
            throw new NotFoundException("Пользователь с id: " + user.getId() + " не найден.");
        }
        userValidate(user);
        log.info("Пользователь с id {} обновлен.", user.getId());
        return userStorage.userUpdate(user);
    }

    public Set<Long> addNewFriend(Long idUser, Long idFriend) {
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при добавлении друга. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idFriend)) {
            log.warn("Ошибка при добавлении друга. Друг с id {} не найден.", idFriend);
            throw new NotFoundException("Друг с id: " + idFriend + " не найден.");
        }

        User user = userStorage.getUsers().get(idUser);
        User friend = userStorage.getUsers().get(idFriend);

        user.getFriends().add(idFriend);
        friend.getFriends().add(idUser);

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", idUser, idFriend);

        userStorage.userUpdate(user);
        userStorage.userUpdate(friend);

        return user.getFriends();
    }

    public Set<Long> deleteFriend(Long idUser, Long idFriend) {
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при удалении друга. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idFriend)) {
            log.warn("Ошибка при удалении друга. Друг с id {} не найден.", idFriend);
            throw new NotFoundException("Друг с id: " + idFriend + " не найден.");
        }

        User user = userStorage.getUsers().get(idUser);
        User friend = userStorage.getUsers().get(idFriend);

        user.getFriends().remove(idFriend);
        friend.getFriends().remove(idUser);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", idUser, idFriend);

        userStorage.userUpdate(user);
        userStorage.userUpdate(friend);

        return user.getFriends();
    }


    public List<User> getAllFriends(Long idUser) {
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при получении списка друзей. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Пользователь с id: " + idUser + " не найден.");
        }

        User user = userStorage.getUsers().get(idUser);
        return user.getFriends().stream()
                .map(friendId -> userStorage.getUsers().get(friendId))
                .collect(Collectors.toList());
    }


    public List<User> getCommonFriends(Long idUser, Long idOther) {
        if (!userStorage.getUsers().containsKey(idUser)) {
            log.warn("Ошибка при получении списка общих друзей. Пользователь с id {} не найден.", idUser);
            throw new NotFoundException("Пользователь с id: " + idUser + " не найден.");
        }
        if (!userStorage.getUsers().containsKey(idOther)) {
            log.warn("Ошибка при получении списка общих друзей. Пользователь с id {} не найден.", idOther);
            throw new NotFoundException("Пользователь с id: " + idOther + " не найден.");
        }

        Set<Long> userFriends = userStorage.getUsers().get(idUser).getFriends();
        Set<Long> otherFriends = userStorage.getUsers().get(idOther).getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(friendId -> userStorage.getUsers().get(friendId))
                .collect(Collectors.toList());
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
