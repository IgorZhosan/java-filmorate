package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userDbRepository;
    private final EventService eventService;

    @Override
    public List<User> getUsers() {
        return userDbRepository.getAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userDbRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
    }

    @Override
    public User addUser(User user) {
        checkName(user);
        return userDbRepository.addUser(user);
    }

    @Override
    public List<User> addFriend(Integer userId, Integer friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        if (Objects.equals(userId, friendId)) {
            log.warn("Ошибка! Нельзя добавить самого себя в друзья");
            throw new ValidationException("Ошибка! Нельзя добавить самого себя в друзья");
        }
        userDbRepository.addFriend(userId, friendId);
        eventService.register(userId, Operation.ADD, EventType.FRIEND, friendId);
        return List.of(userDbRepository.getById(userId).get(), userDbRepository.getById(friendId).get());
    }

    @Override
    public List<User> getFriends(Integer id) {
        checkExistUser(id);
        return userDbRepository.getFriendsByUserId(id);
    }

    @Override
    public User updateUser(User user) {
        log.debug("Изменение параметров пользователя с идентификатором {}", user.getId());
        checkExistUser(user.getId());
        return userDbRepository.updateUser(user);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        checkExistUser(userId);
        checkExistUser(friendId);
        eventService.register(userId, Operation.REMOVE, EventType.FRIEND, friendId);
        userDbRepository.deleteFriend(userId, friendId);
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        checkExistUser(id);
        checkExistUser(otherId);
        return userDbRepository.getCommonFriends(id, otherId);
    }

    @Override
    public Boolean deleteUser(Integer id) {
        checkExistUser(id);
        return userDbRepository.deleteUser(id);
    }

    private void checkExistUser(Integer id) {
        if (userDbRepository.getById(id).isEmpty()) {
            throw new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует");
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public Set<Film> getRecommendations(Integer id) {
        checkExistUser(id);
        return userDbRepository.getRecommendations(id);
    }
}