package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    int id;
    String email;
    String login;
    String name;
    Date birthday;
}
