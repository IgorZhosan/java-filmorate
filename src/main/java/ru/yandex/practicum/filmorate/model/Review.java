package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode(of = "reviewId")
@ToString
public class Review {
    private Integer reviewId;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    @NotNull
    private Boolean isPositive;

    private Integer useful;
}