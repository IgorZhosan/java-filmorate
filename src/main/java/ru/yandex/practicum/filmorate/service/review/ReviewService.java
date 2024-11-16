package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review updateReview(Review review);

    Boolean deleteReview(Integer id);

    Review getReviewById(Integer id);

    List<Review> getReviewsByFilm(Integer filmId, Integer count);

    void setLike(Integer id, Integer userId);

    void setDislike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);
}
