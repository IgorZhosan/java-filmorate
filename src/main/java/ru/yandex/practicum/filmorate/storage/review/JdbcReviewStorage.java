package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.extractor.ReviewExtractor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewExtractor reviewExtractor;

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";
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
        return review;
    }

    @Override
    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, new Object[]{id}, reviewExtractor);
        return reviews.stream().findFirst();
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

        return jdbcTemplate.query(sql, params, reviewExtractor);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "MERGE INTO reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, TRUE)";
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
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }
}
