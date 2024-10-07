package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Set;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendsController {
    private final UserService userService;

    @PutMapping("/{id}/friends/{friendId}") //добавление пользователя в друзья
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> addNewFriend(@PathVariable("id") @Positive Long idUser, @PathVariable("friendId") @Positive Long idFriend) {
        return userService.addNewFriend(idUser, idFriend);
    }

    @DeleteMapping("/{id}/friends/{friendId}") // удаление из друзей пользователя
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> deleteFriend(@PathVariable("id") @Positive Long idUser, @PathVariable("friendId") @Positive Long idFriend) {
        return userService.deleteFriend(idUser, idFriend);
    }
}
