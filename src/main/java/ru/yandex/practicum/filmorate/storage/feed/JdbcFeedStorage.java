package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JdbcFeedStorage implements FeedStorage {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void makeEvent(final int userId, final int friendId, final EventType eventType, final Operation operation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlEvent = "INSERT INTO feed (user_id, timestamp, event_type, operation, entity_id) " +
                "VALUES (:user_id, :timestamp, :event_type, :operation, :entity_id); ";
        Map<String, Object> params = new HashMap<>();

        params.put("user_id", userId);
        params.put("timestamp", Instant.now().toEpochMilli());
        params.put("event_type", eventType.toString());
        params.put("operation", operation.toString());
        params.put("entity_id", friendId);

        jdbc.update(sqlEvent, new MapSqlParameterSource().addValues(params), keyHolder, new String[]{"event_id"});
    }
}