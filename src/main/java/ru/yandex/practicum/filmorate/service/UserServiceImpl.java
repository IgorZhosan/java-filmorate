package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(User user, User friendId) {
        userStorage.getUser(user).setFriend((long) friendId.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        userStorage.getUser(user).deleteFriend((long) friend.getId());
    }

    @Override
    public List<User> getAllFriend(User friendsUser) {
        return List.of();
    }
}
