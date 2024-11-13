package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateReleaseValidation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private Integer id;

    @Size(max = 255, message = "Максимальная длина - 255 символов")
    @NotBlank(message = "Фильм должен быть указан")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @DateReleaseValidation
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDate releaseDate;

    @Min(value = 0, message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private final Set<Integer> likes = new HashSet<>();

    @NotNull
    private Mpa mpa;

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    private LinkedHashSet<Director> directors = new LinkedHashSet<>();
}
