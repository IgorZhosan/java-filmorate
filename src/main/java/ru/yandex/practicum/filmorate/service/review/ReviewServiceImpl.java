package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewDbRepository;
    private final FilmStorage filmDbRepository;
    private final UserStorage userDbRepository;
    private final EventService eventService;

    @Override
    public Review addReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        Review addedReview = reviewDbRepository.addReview(review);
        eventService.register(
                addedReview.getUserId(),
                Operation.ADD,
                EventType.REVIEW,
                addedReview.getReviewId()
        );
        return addedReview;
    }

    @Override
    public Review updateReview(Review review) {
        checkFilmExist(review.getFilmId());
        checkUserExist(review.getUserId());
        checkReviewExist(review.getReviewId());
        Review updatedReview = reviewDbRepository.updateReview(review);
        eventService.register(
                updatedReview.getUserId(),
                Operation.UPDATE,
                EventType.REVIEW,
                updatedReview.getReviewId()
        );
        return updatedReview;
    }

    @Override
    public Boolean deleteReview(Integer id) {
        Review review = getReviewById(id);
        eventService.register(
                review.getUserId(),
                Operation.REMOVE,
                EventType.REVIEW,
                review.getReviewId()
        );
        return reviewDbRepository.deleteReview(id);
    }

    @Override
    public Review getReviewById(Integer id) {
        return reviewDbRepository.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    @Override
    public List<Review> getReviewsByFilm(Integer filmId, Integer count) {
        if (filmId == null) {
            return reviewDbRepository.getAll(count);
        }
        checkFilmExist(filmId);
        return reviewDbRepository.getByFilmId(filmId, count);
    }

    @Override
    public void setLike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, true);
    }

    @Override
    public void setDislike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.setUseful(id, userId, false);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        checkReviewExist(id);
        checkUserExist(userId);
        reviewDbRepository.deleteLike(id, userId);
    }

    private void checkReviewExist(Integer reviewId) {
        reviewDbRepository.getById(reviewId).orElseThrow(() ->
                new NotFoundException("Ошибка! Отзыва с заданным идентификатором не существует"));
    }

    private void checkUserExist(Integer userId) {
        userDbRepository.getById(userId).orElseThrow(() ->
                new NotFoundException("Ошибка! Пользователя с заданным идентификатором не существует"));
    }

    private void checkFilmExist(Integer filmId) {
        filmDbRepository.getById(filmId).orElseThrow(() ->
                new NotFoundException("Ошибка! Фильма с заданным идентификатором не существует"));
    }
}