package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NotBlank(message = "Имейл должен быть указан")
    @Email(message = "Имейл должен содержать символ «@». Формат имейла: example@mail.com")
    private String email;
    @NotBlank(message = "Логин должен быть указан")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
