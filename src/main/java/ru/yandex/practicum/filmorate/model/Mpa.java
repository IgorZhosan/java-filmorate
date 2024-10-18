package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class Mpa {

    @NotNull
    private Integer id;

    @Size(max = 200, message = "Максимальная длина - 200 символов")
    @NotNull
    private String name;
}
