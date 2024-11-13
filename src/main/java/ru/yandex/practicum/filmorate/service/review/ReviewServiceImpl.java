package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;

    public ReviewServiceImpl(@Qualifier("jdbcReviewStorage")ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @Override
    public Review getReview(int id) {
        return reviewStorage.getReviewById(id).orElseThrow(() -> new NotFoundException("Ревью с id: " + id + " не существует."));
    }

    @Override
    public Review reviewCreate(Review review) {
       return reviewStorage.reviewCreate(review);
    }

    @Override
    public Review reviewUpdate(Review review) {
        if (reviewStorage.getReviewById(review.getReviewId()).isEmpty()) {
            log.warn("Ревью с id {} не найдено.", review.getReviewId());
            throw new NotFoundException("Ревью с id: " + review.getReviewId() + " не найдено.");
        }
        return reviewStorage.reviewUpdate(review);
    }

    @Override
    public void deleteReview(int reviewId) {
         reviewStorage.deleteReview(reviewId);
    }

    @Override
    public Collection<Review> getAllReviews(int filmId, int count) {
        return reviewStorage.getAllReviews(filmId, count);
    }

    @Override
    public void addReviewLike(int reviewId, int likeId) {
       reviewStorage.addReviewLike(reviewId, likeId);
    }

    @Override
    public void deleteReviewLike(int reviewId, int userId) {
       reviewStorage.deleteReviewLike(reviewId, userId);
    }

    @Override
    public void addReviewDislike(int reviewId, int userId) {
        reviewStorage.addReviewDislike(reviewId, userId);
    }

    @Override
    public void deleteReviewDislike(int reviewId, int userId) {
        reviewStorage.deleteReviewDislike(reviewId, userId);
    }
}
