package ru.yandex.practicum.filmorate.storage.review.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UsefulReviewsExtractor implements ResultSetExtractor<Integer> {
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        int counter = 0;
        while (rs.next()) {
            counter += rs.getInt("isUseful");
        }
        return counter;
    }
}
