package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    @Override
    public Review createReview(Review review) {
        if (userService.getUserById(review.getUserId()) == null) {
            throw new NotFoundException("Пользователь с id " + review.getUserId() + " не найден");
        }

        if (filmService.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException("Фильм с id " + review.getFilmId() + " не найден");
        }

        return reviewStorage.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        // Проверка на существование отзыва перед обновлением
        getReviewById(review.getReviewId());
        return reviewStorage.updateReview(review);
    }

    @Override
    public void deleteReview(int id) {
        // Проверка на существование отзыва перед удалением
        getReviewById(id);
        reviewStorage.deleteReview(id);
    }

    @Override
    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

    @Override
    public List<Review> getReviews(Integer filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        // Добавляем проверку на существование отзыва перед изменением
        getReviewById(reviewId);
        reviewStorage.updateUseful(reviewId, 1);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        // Проверка на существование отзыва перед изменением
        getReviewById(reviewId);
        reviewStorage.updateUseful(reviewId, -1);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        // Проверка на существование отзыва перед изменением
        getReviewById(reviewId);
        reviewStorage.updateUseful(reviewId, -1);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        // Проверка на существование отзыва перед изменением
        getReviewById(reviewId);
        reviewStorage.updateUseful(reviewId, 1);
    }
}