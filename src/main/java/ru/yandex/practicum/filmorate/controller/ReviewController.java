package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}") //   получение ревью
    public Review getReview(@PathVariable @Positive int id) {
        return reviewService.getReview(id);
    }

    @PostMapping() // для добавления нового ревью
    @ResponseStatus(HttpStatus.CREATED)
    public Review reviewCreate(@Valid @RequestBody Review review) { // значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса
        return reviewService.reviewCreate(review);
    }

    @PutMapping() //для обновления данных существующего ревью
    public Review reviewUpdate(@Valid @RequestBody Review review) {
        return reviewService.reviewUpdate(review);
    }


    @DeleteMapping("/{id}") //удаление ревью
    public void deleteReview(@PathVariable @Positive int id) {
        reviewService.deleteReview(id);
    }

    @GetMapping  // получение списка ревью
    public Collection<Review> getAllReviews(
            @RequestParam(required = false, defaultValue = "0") @Positive int filmId,
            @RequestParam(defaultValue = "10") @Positive int count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addReviewLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.deleteReviewLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.deleteReviewDislike(id, userId);
    }
}
