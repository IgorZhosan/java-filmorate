package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;

    Set<Long> friends = new HashSet<>();

    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "login не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будушем")
    private LocalDate birthday;
}
