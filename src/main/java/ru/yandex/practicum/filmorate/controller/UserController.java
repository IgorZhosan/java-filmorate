package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.userService.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User refreshTheUser(@Valid @RequestBody User user) {
        userService.validateUser(user);
        userService.updateUser(user);
        return userService.getUser((long) user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User putTheUser(@Valid @RequestBody User user) {
        userService.validateUser(user);
        userService.addingUser(user);
        return userService.getUser((long) user.getId());
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.addFriend(id, friendId);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User deleteFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        userService.deleteFriend(id, friendId);
        userService.deleteFriend(friendId, id);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Long> getCommonFriends(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}
