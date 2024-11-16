package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event addEvent(Event event);

    List<Event> getByUserId(Integer userId);

    Optional<Event> getByEventId(Integer eventId);
}