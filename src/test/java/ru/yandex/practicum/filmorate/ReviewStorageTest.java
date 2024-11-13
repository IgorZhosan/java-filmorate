package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewStorageTest {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewStorageTest(@Qualifier("jdbcReviewStorage") ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @Test
    @Order(1)
    void shouldReviewByIdTest() {
        Optional<Review> reviewOptional = reviewStorage.getReviewById(112);
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                            assertThat(review).hasFieldOrPropertyWithValue("reviewId", 112);
                            assertThat(review).hasFieldOrPropertyWithValue("content", "asdsdas");
                            assertThat(review).hasFieldOrPropertyWithValue("isPositive", true);
                            assertThat(review).hasFieldOrPropertyWithValue("userId", 4);
                            assertThat(review).hasFieldOrPropertyWithValue("filmId", 32);
                            assertThat(review).hasFieldOrPropertyWithValue("useful", 1);
                        }
                );
    }

    @Test
    @Order(2)
    void getAllReviewsByFilmIdTest() {
        Optional<Collection<Review>> reviewOptional = Optional.ofNullable(reviewStorage.getAllReviews(32, 2));
        assertThat(reviewOptional);
    }
}
