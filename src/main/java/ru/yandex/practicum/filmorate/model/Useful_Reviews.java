package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Useful_Reviews {
    private Integer reviewId;
    private Integer filmId;
    private Integer userId;
    private Integer isUseful;
}
