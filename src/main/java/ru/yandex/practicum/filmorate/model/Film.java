package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
