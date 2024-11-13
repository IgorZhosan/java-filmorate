package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.extractor.ReviewExtractor;
import ru.yandex.practicum.filmorate.storage.review.extractor.ReviewsExtractor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewStorage implements ReviewStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final ReviewExtractor reviewExtractor;
    private final ReviewsExtractor reviewsExtractor;

    @Override
    public Collection<Review> getAllReviews(int filmId, int count) {
        String sql = "SELECT * " +
                "FROM REVIEWS r " +
                "WHERE r.FILMID = :filmId LIMIT :count;";

        Map<Integer, Review> reviewMap = jdbc.query(sql, Map.of("filmId", filmId, "count", count), reviewsExtractor);
        assert reviewMap != null;

        return reviewMap.values().stream().toList();
    }

    @Override
    public Collection<Review> getAllReviews(int count) {
        String sql = "SELECT * " +
                "FROM REVIEWS r " +
                "JOIN PUBLIC.USEFUL_REVIEWS UR on r.REVIEWID = UR.REVIEWID LIMIT :count;";

        Map<Integer, Review> reviewMap = jdbc.query(sql, Map.of("count", count), reviewsExtractor);
        assert reviewMap != null;

        return reviewMap.values().stream().toList();
    }

    @Override
    public Review reviewCreate(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO REVIEWS (REVIEWID, CONTENT, ISPOSITIVE, USERID, FILMID) " +
                "VALUES (:reviewId, :content, :isPositive, :userId, :filmId); ";
        Map<String, Object> params = new HashMap<>();

        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());
        params.put("userId", review.getUserId());
        params.put("filmId", review.getFilmId());

        jdbc.update(sql, new MapSqlParameterSource().addValues(params), keyHolder, new String[]{"reviewId"});
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    @Override
    public Review reviewUpdate(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT = :content, " +
                "ISPOSITIVE = :isPositive, " +
                "USERID = :userId, " +
                "FILMID = :filmId " +
                "WHERE REVIEWID = :reviewId;";
        Map<String, Object> params = new HashMap<>();

        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());
        params.put("userId", review.getUserId());
        params.put("filmId", review.getFilmId());
        params.put("reviewId", review.getReviewId());

        jdbc.update(sql, params);

        return review;
    }

    @Override
    public Optional<Review> getReviewById(int reviewId) {
        String sql = "SELECT * " +
                "FROM REVIEWS r " +
                "JOIN PUBLIC.USEFUL_REVIEWS UR on r.REVIEWID = UR.REVIEWID " +
                "WHERE r.REVIEWID = :reviewId;";
        Review review = jdbc.query(sql, Map.of("reviewId", reviewId), reviewExtractor);
        return Optional.ofNullable(review);
    }

    @Override
    public void addReviewLike(int reviewId, int userId) {
        String sql = "MERGE INTO USEFUL_REVIEWS(REVIEWID, USER_ID, ISUSEFUL) VALUES (:reviewId, :userId, :isUseful); ";
        jdbc.update(sql, Map.of("reviewId", reviewId, "userId", userId, "isUseful", 1));
    }

    @Override
    public void deleteReviewLike(int reviewId, int userId) {
        String sql = "DELETE FROM USEFUL_REVIEWS WHERE REVIEWID = :reviewId AND USER_ID = :userId AND ISUSEFUL = 1;";
        jdbc.update(sql, Map.of("reviewId", reviewId, "userId", userId));
    }

    @Override
    public void addReviewDislike(int reviewId, int userId) {
        String sql = "MERGE INTO USEFUL_REVIEWS(REVIEWID, USER_ID, ISUSEFUL) VALUES (:reviewId, :userId, :isUseful); ";
        jdbc.update(sql, Map.of("reviewId", reviewId, "userId", userId, "isUseful", -1));
    }

    @Override
    public void deleteReviewDislike(int reviewId, int userId) {
        String sql = "DELETE FROM USEFUL_REVIEWS WHERE REVIEWID = :reviewId AND USER_ID = :userId AND ISUSEFUL = -1";
        jdbc.update(sql, Map.of("reviewId", reviewId, "userId", userId));
    }
}
