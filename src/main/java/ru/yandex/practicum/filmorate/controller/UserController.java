package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final List<User> userList = new ArrayList<>(users.values());

    @GetMapping("/get")
    public List<User> getAllUsers() {
        if (!users.isEmpty()) {
            return List.copyOf(userList);
        } else {
            throw new InvalidUserException("Список пользователей пуст");
        }
    }

    @PatchMapping("/refresh")
    public User refreshTheUser(@RequestBody User user) {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @PostMapping("/put")
    public User putTheUser(@RequestBody User user) {
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidUserException("Некорректный email, он должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new InvalidUserException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new InvalidUserException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
