package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping //получение списка пользователей.
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping() // для добавления нового пользователя в список.
    @ResponseStatus(HttpStatus.CREATED)
    public User userCreate(@Valid @RequestBody User user) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        return userService.userCreate(user);
    }

    @PutMapping() //для обновления данных существующего пользователя.
    public User userUpdate(@Valid @RequestBody User user) {
        return userService.userUpdate(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}") //добавление пользователя в друзья
    public void addNewFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        userService.addNewFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}") // удаление из друзей пользователя
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends") // получение списка друзей пользователя
    public List<User> getAllFriends(@PathVariable("id") int userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}") // получение списка общих друзей с пользователем
    public List<User> getCommonFriends(@PathVariable("id") int userId, @PathVariable int otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendations(@PathVariable("id") @Positive int id) {
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeedOfUser(@PathVariable("id") @Positive int id) {
        return userService.getFeedOfUser(id);
    }
}