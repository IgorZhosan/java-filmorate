package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Review {

    private Integer reviewId;

    private String content;

    @JsonProperty("isPositive")
    private Boolean positive;

    private Integer userId;

    private Integer filmId;

    private int useful = 0;
}
