package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

public interface FeedStorage {
    void makeEvent(int userId, int friendId, EventType eventType, Operation operation);
}
