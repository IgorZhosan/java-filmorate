package ru.yandex.practicum.filmorate.storage.review;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Collection<Review> getAllReviews(int filmId, int count);

    Collection<Review> getAllReviews(int count);

    Review reviewCreate(Review film);

    Review reviewUpdate(Review film);

    void deleteReview(int reviewId);

    Optional<Review> getReviewById(int id);

    void addReviewLike(int reviewId, int userId);

    void deleteReviewLike(int reviewId, int userId);

    void addReviewDislike(int reviewId, int userId);

    void deleteReviewDislike(int reviewId, int userId);

}
