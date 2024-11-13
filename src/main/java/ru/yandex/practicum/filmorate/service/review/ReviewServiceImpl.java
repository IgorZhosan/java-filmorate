package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;

    public ReviewServiceImpl(@Qualifier("jdbcReviewStorage")ReviewStorage reviewStorage, UserService userService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
    }

    @Override
    public Review getReview(int id) {
        return null;
    }

    @Override
    public Review reviewCreate(Review review) {
return null;
    }

    @Override
    public Review reviewUpdate(Review review) {
        return null;
    }

    @Override
    public void deleteReview(int reviewId) {

    }

    @Override
    public List<Review> getAllReviews(int filmId, int count) {
        return List.of();
    }

    @Override
    public void addReviewLike(int reviewId, int likeId) {

    }

    @Override
    public void deleteReviewLike(int reviewId, int userId) {

    }

    @Override
    public void addReviewDislike(int reviewId, int userId) {

    }

    @Override
    public void deleteReviewDislike(int reviewId, int userId) {

    }
}
