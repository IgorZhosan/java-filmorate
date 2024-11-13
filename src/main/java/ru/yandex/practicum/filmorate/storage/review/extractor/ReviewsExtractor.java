package ru.yandex.practicum.filmorate.storage.review.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ReviewsExtractor implements ResultSetExtractor<Map<Integer, Review>> {

    private final NamedParameterJdbcOperations jdbc;
    private final UsefulReviewsExtractor usefulReviewsExtractor;

    @Autowired
    public ReviewsExtractor(NamedParameterJdbcOperations jdbc, UsefulReviewsExtractor usefulReviewsExtractor) {
        this.jdbc = jdbc;
        this.usefulReviewsExtractor = usefulReviewsExtractor;
    }

    @Override
    public Map<Integer, Review> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Review> reviewMap = new LinkedHashMap<>();
        while (rs.next()) {
            var reviewId = rs.getInt("reviewId");
            Review review = new Review();
            var filmId = rs.getInt("filmId");
            review.setReviewId(reviewId);
            review.setContent(rs.getString("content"));
            review.setIsPositive(rs.getBoolean("isPositive"));
            review.setFilmId(filmId);
            review.setUserId(rs.getInt("userId"));

            var sql = "SELECT ISUSEFUL FROM USEFUL_REVIEWS WHERE USEFUL_REVIEWS.REVIEWID " +
                    "IN (SELECT REVIEWID FROM REVIEWS WHERE FILMID = :filmId) AND REVIEWID = :reviewId";

            var useful =  jdbc.query(sql, Map.of("filmId", filmId, "reviewId", reviewId), usefulReviewsExtractor);
            review.setUseful(useful);
            reviewMap.put(reviewId, review);
        }
        return reviewMap;
    }
}


