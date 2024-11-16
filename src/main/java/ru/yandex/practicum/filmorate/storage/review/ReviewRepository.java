package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review addReview(Review review);

    Optional<Review> getById(Integer id);

    List<Review> getAll(Integer count);

    List<Review> getByFilmId(Integer filmId, Integer count);

    Boolean deleteReview(Integer id);

    void setUseful(Integer id, Integer userId, Boolean isUseful);

    void deleteLike(Integer id, Integer userId);

    Review updateReview(Review review);
}