package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.JdbcFeedStorage;
import ru.yandex.practicum.filmorate.storage.review.extractor.ReviewExtractor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewExtractor reviewExtractor;
    private final JdbcFeedStorage jdbcFeedStorage;

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setObject(2, review.getPositive(), java.sql.Types.BOOLEAN);
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        jdbcFeedStorage.makeEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getPositive(),
                review.getReviewId()
        );
        jdbcFeedStorage.makeEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return review;
    }

    @Override
    public void deleteReview(int id) {
        Review review = getReviewById(id).get();
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcFeedStorage.makeEvent(review.getUserId(), id, EventType.REVIEW, Operation.REMOVE);
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, new Object[]{id}, reviewExtractor);
        if (reviews.isEmpty()) {
            return Optional.empty();
        }
        Review review = reviews.get(0);
        review.setUseful(getUsefulCount(id));
        return Optional.of(review);
    }

    @Override
    public List<Review> getReviews(Integer filmId, int count) {
        String sql;
        Object[] params;

        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            params = new Object[]{count};
        } else {
            sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            params = new Object[]{filmId, count};
        }

        List<Review> reviews = jdbcTemplate.query(sql, params, reviewExtractor);
        for (Review review : reviews) {
            review.setUseful(getUsefulCount(review.getReviewId()));
        }
        return reviews;
    }

    private int getUsefulCount(int reviewId) {
        String sql = "SELECT (COUNT(CASE WHEN is_useful = TRUE THEN 1 END) - COUNT(CASE WHEN is_useful = FALSE THEN 1 END)) AS useful " +
                "FROM reviews_likes WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{reviewId}, Integer.class);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "MERGE INTO reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, TRUE)";
        jdbcFeedStorage.makeEvent(userId, reviewId, EventType.LIKE, Operation.ADD);
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sql = "MERGE INTO reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sql, reviewId, userId);

    }

    @Override
    public void removeLike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        jdbcFeedStorage.makeEvent(userId, reviewId, EventType.LIKE, Operation.REMOVE);
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }
}