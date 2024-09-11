package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.util.Date;

@Data
public class Film {
    int id;
    String name;
    String description;
    Date releadeDate;
    Duration duration;
}
