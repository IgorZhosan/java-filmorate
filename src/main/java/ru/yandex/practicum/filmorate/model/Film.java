package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.yandex.practicum.filmorate.util.MinimumDate;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Film {
    private Integer id;

    @NotNull
    private Mpa mpa;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма должно быть не более 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @MinimumDate
    private LocalDate releaseDate;

    @Min(value = 0, message = "Продолжительность фильма не может быть отрицательной")
    private Integer duration;

    private LinkedHashSet<Genre> genres;

    private LinkedHashSet<Director> directors;
}