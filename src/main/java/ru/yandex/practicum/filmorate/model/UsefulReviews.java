package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UsefulReviews {
    private Integer reviewId;
    private Integer filmId;
    private Integer userId;
    private Integer isUseful;
}
