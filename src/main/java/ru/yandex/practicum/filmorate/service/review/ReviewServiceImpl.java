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
        // Проверка на существование отзыва перед добавлением лайка
        if (getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }

        // Проверка на существование пользователя перед добавлением лайка
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        // Проверка на существование отзыва перед добавлением дизлайка
        if (getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }

        // Проверка на существование пользователя перед добавлением дизлайка
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        // Проверка на существование отзыва перед удалением лайка
        if (getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }

        // Проверка на существование пользователя перед удалением лайка
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        reviewStorage.removeLike(reviewId, userId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        // Проверка на существование отзыва перед удалением дизлайка
        if (getReviewById(reviewId) == null) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден");
        }

        // Проверка на существование пользователя перед удалением дизлайка
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        reviewStorage.removeDislike(reviewId, userId);
    }
}