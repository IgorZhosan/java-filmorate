package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.Timestamp;

@Data
@Builder
@EqualsAndHashCode(of = "eventId")
@ToString
public class Event {
    private Integer eventId;

    @NotNull
    private Integer userId;

    private Integer entityId;

    private EventType eventType;

    private Operation operation;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp timestamp;
}