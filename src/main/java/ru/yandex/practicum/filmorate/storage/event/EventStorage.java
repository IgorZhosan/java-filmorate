package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.base.BaseStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EventStorage extends BaseStorage<Event> implements EventRepository {
    private static final String SQL_INSERT_EVENT =
            "INSERT INTO events (user_id, event_type, operation, entity_id, timestamp) " +
                    "VALUES (:user_id, :event_type, :operation, :entity_id, :timestamp);";

    private static final String SQL_GET_EVENT_BY_USER_ID =
            "SELECT * FROM events WHERE user_id = :user_id";

    private static final String SQL_GET_EVENT_BY_EVENT_ID =
            "SELECT * FROM events WHERE event_id = :event_id";

    public EventStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Event addEvent(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", event.getUserId());
        params.addValue("event_type", event.getEventType().name());
        params.addValue("operation", event.getOperation().name());
        params.addValue("entity_id", event.getEntityId());
        params.addValue("timestamp", event.getTimestamp());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(
                SQL_INSERT_EVENT,
                params,
                keyHolder
        );

        event.setEventId(keyHolder.getKeyAs(Integer.class));

        return event;
    }

    @Override
    public List<Event> getByUserId(Integer userId) {
        return getMany(
                SQL_GET_EVENT_BY_USER_ID,
                Map.of("user_id", userId)
        );
    }

    @Override
    public Optional<Event> getByEventId(Integer eventId) {
        return getOne(
                SQL_GET_EVENT_BY_EVENT_ID,
                Map.of("event_id", eventId)
        );
    }
}