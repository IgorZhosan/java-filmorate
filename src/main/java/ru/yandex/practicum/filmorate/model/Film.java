package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;
import java.util.Date;

@Data
public class Film {

    @NotNull
    int id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @NotBlank(message = "Описание не может быть пустым")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date releaseDate;

    @NotNull(message = "Длительность не может быть пустой")
    Duration duration;
}
