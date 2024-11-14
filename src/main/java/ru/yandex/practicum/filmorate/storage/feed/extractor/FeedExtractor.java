package ru.yandex.practicum.filmorate.storage.feed.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedExtractor implements ResultSetExtractor<List<Feed>> {

    @Override
    public List<Feed> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        List<Feed> events = new ArrayList<>();
        while (rs.next()) {
            Feed feed = new Feed();
            feed.setEventId((rs.getInt("event_id")));
            feed.setUserId(rs.getInt("user_id"));
            feed.setTimestamp(rs.getLong("timestamp"));
            feed.setEventType(EventType.valueOf(rs.getString("event_type")));
            feed.setOperation(Operation.valueOf(rs.getString("operation")));
            feed.setEntityId(rs.getInt("entity_id"));
            events.add(feed);
        }
        return events;
    }
}