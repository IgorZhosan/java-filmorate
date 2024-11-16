package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventRepository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final UserStorage userDbRepository;

    @Override
    public void register(Integer userId, Operation operation, EventType eventType, Integer entityId) {
        Event event = Event.builder()
                .userId(userId)
                .operation(operation)
                .eventType(eventType)
                .entityId(entityId)
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        repository.addEvent(event);
    }

    @Override
    public List<Event> getByUserId(Integer userId) {
        if (userDbRepository.getById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} нет.", userId);
            throw new NotFoundException("Пользователя с id = " + userId + " нет.");
        }
        List<Event> feeds = repository.getByUserId(userId);
        if (feeds.isEmpty()) {
            throw new NotFoundException("У данного пользователя нет действий");
        }

        return feeds;
    }

    @Override
    public Event getByEventId(Integer eventId) {
        return repository.getByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с указанным идентификатором не существует"));
    }
}