package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenreStorageTest {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreStorageTest(@Qualifier("jdbcGenreStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Test
    void shouldGetAllGenresTest() {
        Optional<List<Genre>> genresOptional = Optional.ofNullable(genreStorage.getAllGenres());
        assertThat(genresOptional)
                .isPresent()
                .hasValueSatisfying(genres -> {
                            assertThat(genres).isNotEmpty();
                            assertThat(genres).hasSize(6);
                            assertThat(genres).element(0).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(genres).element(0)
                                    .hasFieldOrPropertyWithValue("name", "Комедия");
                            assertThat(genres).element(1).hasFieldOrPropertyWithValue("id", 2);
                            assertThat(genres).element(1)
                                    .hasFieldOrPropertyWithValue("name", "Драма");
                            assertThat(genres).element(2).hasFieldOrPropertyWithValue("id", 3);
                            assertThat(genres).element(2)
                                    .hasFieldOrPropertyWithValue("name", "Мультфильм");
                            assertThat(genres).element(3).hasFieldOrPropertyWithValue("id", 4);
                            assertThat(genres).element(3)
                                    .hasFieldOrPropertyWithValue("name", "Триллер");
                            assertThat(genres).element(4).hasFieldOrPropertyWithValue("id", 5);
                            assertThat(genres).element(4)
                                    .hasFieldOrPropertyWithValue("name", "Документальный");
                            assertThat(genres).element(5).hasFieldOrPropertyWithValue("id", 6);
                            assertThat(genres).element(5)
                                    .hasFieldOrPropertyWithValue("name", "Боевик");
                        }
                );
    }

    @Test
    void shouldGetGenreByIdTest() {
        Optional<Genre> genreOp = genreStorage.getGenreById(1);
        assertThat(genreOp)
                .isPresent()
                .hasValueSatisfying(genres -> {
                    assertThat(genres).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(genres).hasFieldOrPropertyWithValue("name", "Комедия");
                });
    }

    @Test
    void shouldGetGenresListTest() {
        List<Integer> listGenres = List.of(1, 2, 3);
        Optional<Collection<Genre>> genresOptional = Optional.ofNullable(genreStorage.getGenresList(listGenres));
        assertThat(genresOptional)
                .isPresent()
                .hasValueSatisfying(genres -> {
                    assertThat(genres).isNotEmpty();
                    assertThat(genres).hasSize(3);
                    assertThat(genres).element(0).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(genres).element(0).hasFieldOrPropertyWithValue("name", "Комедия");
                    assertThat(genres).element(1).hasFieldOrPropertyWithValue("id", 2);
                    assertThat(genres).element(1).hasFieldOrPropertyWithValue("name", "Драма");
                    assertThat(genres).element(2).hasFieldOrPropertyWithValue("id", 3);
                    assertThat(genres).element(2).hasFieldOrPropertyWithValue("name", "Мультфильм");
                });
    }
}
