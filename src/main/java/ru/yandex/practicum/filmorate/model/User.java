package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    @NotNull
    int id;

    @NotBlank(message = "email не может быть пустым")
    String email;

    @NotBlank(message = "login не может быть пустым")
    String login;

    @NotBlank(message = "name не может быть пустым")
    String name;

    @NotNull
    Date birthday;

    public User(int id, String email, String login, Date birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
}
