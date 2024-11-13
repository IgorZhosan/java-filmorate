package ru.yandex.practicum.filmorate.storage.review.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewExtractor implements ResultSetExtractor<Review> {

    @Override
    public Review extractData(final ResultSet rs) throws SQLException, DataAccessException {

        Review review = null;
        if (rs.first()) {
            review = new Review();
            review.setReviewId(rs.getInt("reviewId"));
            review.setContent(rs.getString("content"));
            review.setIsPositive(rs.getBoolean("isPositive"));
            review.setFilmId(rs.getInt("filmId"));
            review.setUserId(rs.getInt("userId"));

            var isUseful = rs.getInt("isUseful");
            if (isUseful != 0) {
                int counter = 0;
                do {
                    counter += rs.getInt("isUseful");
                } while (rs.next());
                review.setUseful(counter);
            }
        }
        return review;
    }
}
