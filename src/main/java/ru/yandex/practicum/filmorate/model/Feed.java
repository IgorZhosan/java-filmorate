package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
public class Feed {
    private Integer eventId;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer entityId;

    private Long timestamp;

    private EventType eventType;

    private Operation operation;
}