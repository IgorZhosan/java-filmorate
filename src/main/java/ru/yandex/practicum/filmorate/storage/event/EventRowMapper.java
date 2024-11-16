package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .timestamp(rs.getTimestamp("timestamp"))
                .build();
    }
}