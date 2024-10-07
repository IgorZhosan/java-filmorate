package ru.yandex.practicum.filmorate.controller.userController;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
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
