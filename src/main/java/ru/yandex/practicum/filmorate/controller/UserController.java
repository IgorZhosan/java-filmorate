package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int userIdSequence = 1;

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());  // Возвращаем пустой список вместо ошибки
    }

    @PutMapping
    public User refreshTheUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким ID не найден");
        }
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @PostMapping
    public User putTheUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            validateUser(user);
            user.setId(userIdSequence++);
            users.put(user.getId(), user);
            return user;
        } else throw new ValidationException("Такой пользователь уже есть");
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
