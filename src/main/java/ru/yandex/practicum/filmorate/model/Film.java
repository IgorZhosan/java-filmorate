package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    private int id;

    Set<Long> likes = new HashSet<>();

    @NotBlank
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    public void setLikes(Long idUser) {
        if (!likes.contains(idUser)) {
            likes.add(idUser);
        } else throw new ValidationException("Такой лайк уже есть");
    }

    public void deleteLike(Long idUser) {
        if (likes.contains(idUser)) {
            likes.remove(idUser);
        } else throw new ValidationException("Такого лайка нет");
    }
}