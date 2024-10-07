package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override //получение списка пользователей.
    public Collection<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override // для добавления нового пользователя в список.
    public User userCreate(User user) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        user.setId(getIdNext());
        users.put(user.getId(), user);
        return user;
    }

    @Override //для обновления данных существующего пользователя.
    public User userUpdate(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public Set<Long> addNewFriend(Long idUser, Long idFriend) { //добавление пользователя в друзья
        users.get(idUser).getFriends().add(idFriend);
        users.get(idFriend).getFriends().add(idUser);
        return users.get(idUser).getFriends();
    }

    @Override
    public Set<Long> deleteFriend(Long idUser, Long idFriend) { // удаление из друзей пользователя
        users.get(idUser).getFriends().remove(idFriend);
        users.get(idFriend).getFriends().remove(idUser);
        return users.get(idUser).getFriends();
    }

    @Override
    public List<User> getAllFriends(Long idUser) { // получение списка друзей пользователя
        return users.get(idUser).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long idUser, Long idOther) { // получение списка общих друзей с пользователем
        Set<Long> user = users.get(idUser).getFriends();
        Set<Long> userOther = users.get(idOther).getFriends();
        return user.stream()
                .filter(userOther::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private long getIdNext() {
        return ++currentId;
    }
}
