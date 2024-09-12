package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/registration")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/get")
    public Map<Integer, User> getAllUsers() {
        if (!users.isEmpty()) {
            return users;
        } else {
            throw new InvalidUserException("Список пользователей пуст");
        }
    }

    @PatchMapping("/refresh")
    public User refreshTheUser(@Valid @RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidUserException("задай email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new InvalidUserException("задай login");
        }

        if (user.getBirthday() == null) {
            throw new InvalidUserException("задай birthday");
        }

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @PostMapping("/put")
    public User putTheUser(@Valid @RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidUserException("задай email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new InvalidUserException("задай login");
        }

        if (user.getBirthday() == null) {
            throw new InvalidUserException("задай birthday");
        }

        users.put(user.getId(), user);
        return user;
    }
}
