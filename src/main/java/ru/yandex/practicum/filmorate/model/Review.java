package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {

    private Integer reviewId;

    @NotBlank
    private String content;

    @JsonProperty("isPositive")
    @NotNull
    private Boolean positive;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    private int useful;
}