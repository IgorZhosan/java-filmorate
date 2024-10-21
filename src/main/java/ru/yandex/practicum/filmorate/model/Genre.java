package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Genre {
    private Integer id;

    @Size(max = 200, message = "Максимальная длина 200 символов")
    @NotBlank(message = "Название жанра не должно быть пустым")
    private String name;
}
