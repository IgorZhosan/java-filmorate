package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateReleaseValidation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Фильм должен быть указан")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @DateReleaseValidation
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    private final Set<Long> likes = new HashSet<>();
}
