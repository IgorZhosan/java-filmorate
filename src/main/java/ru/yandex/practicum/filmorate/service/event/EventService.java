package ru.yandex.practicum.filmorate.service.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.util.List;

public interface EventService {
    void register(Integer userId,
                  Operation operation,
                  EventType eventType,
                  Integer entityId);

    List<Event> getByUserId(Integer userId);

    Event getByEventId(Integer eventId);
}
