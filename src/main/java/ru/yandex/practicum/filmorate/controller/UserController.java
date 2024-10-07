package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping //получение списка пользователей.
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        return List.copyOf(userService.getAllUsers());
    }

    @PostMapping() // для добавления нового пользователя в список.
    @ResponseStatus(HttpStatus.CREATED)
    public User userCreate(@Valid @RequestBody User user) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        return userService.userCreate(user);
    }

    @PutMapping() //для обновления данных существующего пользователя.
    @ResponseStatus(HttpStatus.OK)
    public User userUpdate(@Valid @RequestBody User user) {
        return userService.userUpdate(user);
    }



    @GetMapping("/{id}/friends") // получение списка друзей пользователя
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllFriends(@PathVariable("id") @Positive Long idUser) {
        return userService.getAllFriends(idUser);
    }

    @GetMapping("/{id}/friends/common/{otherId}") // получение списка общих друзей с пользователем
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriends(@PathVariable("id") @Positive Long idUser, @PathVariable("otherId") @Positive Long idOther) {
        return userService.getCommonFriends(idUser, idOther);
    }
}