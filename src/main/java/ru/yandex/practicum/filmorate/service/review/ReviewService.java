package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review getReview(int id);

    Review reviewCreate(Review review);

    Review reviewUpdate(Review review);

    void deleteReview(int reviewId);

    Collection<Review> getAllReviews(int reviewId, int count);

    void addReviewLike(int reviewId, int userId);

    void deleteReviewLike(int reviewId, int userId);

    void addReviewDislike(int reviewId, int userId);

    void deleteReviewDislike(int reviewId, int userId);
}
