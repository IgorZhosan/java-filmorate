package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 0;

    @Override //получение списка пользователей.
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override // для добавления нового пользователя в список.
    public User userCreate(final User user) {
        user.setId(getIdNext());
        users.put(user.getId(), user);
        return user;
    }

    @Override //для обновления данных существующего пользователя.
    public User userUpdate(final User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override // получение пользователя по id
    public Optional<User> getUserById(final int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addNewFriend(final int userId, final int friendId) { //добавление пользователя в друзья
        final Set<Integer> userFriends = users.get(userId).getFriends();
        userFriends.add(friendId);
        users.get(friendId).getFriends().add(userId);
    }


    @Override
    public void deleteFriend(final int userId, final int friendId) { // удаление из друзей пользователя
        final Set<Integer> userFriends = users.get(userId).getFriends();
        userFriends.remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getAllFriends(final int userId) { // получение списка друзей пользователя
        return users.get(userId).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(final int userId, final int otherId) { // получение списка общих друзей с пользователем
        Set<Integer> user = users.get(userId).getFriends();
        Set<Integer> userOther = users.get(otherId).getFriends();
        return user.stream()
                .filter(userOther::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private Integer getIdNext() {
        return ++currentId;
    }
}