package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class User {
    private Integer id;

    @NotBlank(message = "Электронная почта должна быть задана")
    @Email
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}