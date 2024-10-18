package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;

    @NotBlank(message = "Имейл должен быть указан")
    @Email(message = "Имейл должен содержать символ «@». Формат имейла: example@mail.com")
    private String email;

    @NotBlank(message = "Логин должен быть указан")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    @Size(max = 255, message = "Максимальная длина - 255 символов")
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будушем")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
}
