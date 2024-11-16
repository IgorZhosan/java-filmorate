package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.base.BaseStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewStorage extends BaseStorage<Review> implements ReviewRepository {

    private final ReviewRowMapper reviewRowMapper;

    public ReviewStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper,
                         ReviewRowMapper reviewRowMapper) {
        super(jdbc, mapper);
        this.reviewRowMapper = reviewRowMapper;
    }

    private static final String SQL_INSERT_REVIEW =
            "INSERT INTO reviews (content, is_positive, film_id, user_id) " +
                    "VALUES (:content, :is_positive, :film_id, :user_id);";

    private static final String SQL_GET_REVIEW_BASE =
            "SELECT pos.review_id, pos.content, pos.film_id, pos.user_id, pos.is_positive, (pos.positive - neg.negative)" +
                    " AS useful FROM " +
                    "(SELECT r.review_id, r.content, r.film_id, r.user_id, r.is_positive, COUNT(pos.review_id) " +
                    "AS positive FROM REVIEWS r " +
                    "LEFT JOIN " +
                    "(SELECT rl.review_id, rl.is_useful FROM REVIEWS_LIKES rl " +
                    "WHERE rl.IS_USEFUL = TRUE) pos " +
                    "ON r.REVIEW_ID = pos.review_id " +
                    "GROUP BY r.review_id) pos " +
                    "JOIN " +
                    "(SELECT r.review_id, r.content, r.film_id, r.user_id, r.is_positive, COUNT(neg.review_id) AS negative FROM REVIEWS r " +
                    "LEFT JOIN " +
                    "(SELECT rl.review_id, rl.is_useful FROM REVIEWS_LIKES rl " +
                    "WHERE rl.IS_USEFUL = FALSE) neg " +
                    "ON r.REVIEW_ID = neg.review_id " +
                    "GROUP BY r.review_id) neg " +
                    "ON pos.review_id = neg.review_id ";

    private static final String SQL_GET_REVIEW_BY_IDs = SQL_GET_REVIEW_BASE +
            "WHERE pos.review_id IN (:ids) ORDER BY useful DESC;";

    private static final String SQL_GET_ALL_REVIEWS_LIMIT = SQL_GET_REVIEW_BASE +
            "ORDER BY useful DESC LIMIT :count;";

    private static final String SQL_GET_REVIEW_BY_FILM_IDs_LIMIT = SQL_GET_REVIEW_BASE
            + "WHERE pos.film_id = :id ORDER BY useful DESC LIMIT :count;";

    private static final String SQL_DELETE_REVIEW_BY_ID =
            "DELETE FROM reviews WHERE review_id = :id;";

    private static final String SQL_MERGE_REVIEW_LIKES =
            "MERGE INTO reviews_likes (review_id, user_id, is_useful) " +
                    "VALUES (:review_id, :user_id, :is_useful);";

    private static final String SQL_DELETE_LIKE =
            "DELETE FROM reviews_likes WHERE (review_id = :review_id AND user_id = :user_id);";

    private static final String SQL_UPDATE_REVIEW = "UPDATE reviews SET content = :content, " +
            "is_positive=:is_positive " +
            "WHERE review_id = :review_id; ";

    @Override
    public Review addReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", review.getContent());
        params.addValue("is_positive", review.getIsPositive());
        params.addValue("film_id", review.getFilmId());
        params.addValue("user_id", review.getUserId());
        jdbc.update(SQL_INSERT_REVIEW, params, keyHolder);
        review.setReviewId(keyHolder.getKeyAs(Integer.class));
        review = getById(review.getReviewId()).orElseThrow(() -> new NotFoundException("Ошибка при добавлении отзыва"));
        return review;
    }

    @Override
    public Optional<Review> getById(Integer id) {
        List<Integer> ids = List.of(id);
        Map<String, Object> params = Map.of("ids", ids);
        Optional<Review> review;
        try {
            review = getOne(SQL_GET_REVIEW_BY_IDs, params);
        } catch (EmptyResultDataAccessException ignored) {
            review = Optional.empty();
        }
        return review;
    }

    @Override
    public List<Review> getAll(Integer count) {
        Map<String, Object> params = Map.of("count", count);
        return getMany(SQL_GET_ALL_REVIEWS_LIMIT, params);
    }

    @Override
    public List<Review> getByFilmId(Integer filmId, Integer count) {
        Map<String, Object> params = Map.of("id", filmId, "count", count);
        return getMany(SQL_GET_REVIEW_BY_FILM_IDs_LIMIT, params);
    }

    @Override
    public Boolean deleteReview(Integer id) {
        return (jdbc.update(SQL_DELETE_REVIEW_BY_ID, Map.of("id", id)) == 1);
    }

    @Override
    public void setUseful(Integer id, Integer userId, Boolean isUseful) {
        Map<String, Object> params = Map.of(
                "review_id", id,
                "user_id", userId,
                "is_useful", isUseful);
        jdbc.update(SQL_MERGE_REVIEW_LIKES, params);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        Map<String, Object> params = Map.of(
                "review_id", id,
                "user_id", userId);
        jdbc.update(SQL_DELETE_LIKE, params);
    }

    @Override
    public Review updateReview(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("review_id", review.getReviewId());
        params.addValue("user_id", review.getUserId());
        params.addValue("content", review.getContent());
        params.addValue("film_id", review.getFilmId());
        params.addValue("is_positive", review.getIsPositive());
        jdbc.update(SQL_UPDATE_REVIEW, params);
        return getById(review.getReviewId()).get();
    }
}