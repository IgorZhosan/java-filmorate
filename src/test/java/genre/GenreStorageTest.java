package genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({GenreStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("GenreStorageTest")
class GenreStorageTest {

    private final GenreStorage genreStorage;

    public static final Integer FIRST_GENRE_ID = 1;
    public static final Integer SECOND_GENRE_ID = 2;

    public static final Integer TEST_FILM_ID = 1;
    public static final Integer TEST_FILM_COUNT_GENRES = 2;
    public static final Integer DEFAULT_COUNT_GENRES = 6;

    static Genre getFirstGenre() {
        return Genre.builder().id(FIRST_GENRE_ID).name("Комедия").build();
    }

    static Genre getSecondGenre() {
        return Genre.builder().id(SECOND_GENRE_ID).name("Драма").build();
    }

    @Test
    @DisplayName("Тест получения всех жанров")
    void getAll_ReturnCorrectListGenres() {
        List<Genre> listGenres = genreStorage.getAll();
        Assertions.assertEquals(listGenres.size(), DEFAULT_COUNT_GENRES);
    }

    @Test
    @DisplayName("Тест получения жанра по идентификатору")
    void getById_GenreExist_ReturnCorrectGenre() {

        // when
        Optional<Genre> genre = genreStorage.getById(FIRST_GENRE_ID);

        // then
        assertThat(genre)
                .isPresent()
                .get()
                .usingRecursiveAssertion()
                .isEqualTo(getFirstGenre());
    }

    @Test
    @DisplayName("Тест получения жанров по идентификатору фильма")
    void getGenresByFilmId_ReturnCorrectGenres() {

        // when
        Set<Genre> genres = genreStorage.getGenresByFilmId(TEST_FILM_ID);

        // then
        Assertions.assertEquals(genres.size(), TEST_FILM_COUNT_GENRES);
        Assertions.assertTrue(genres.contains(getFirstGenre()));
        Assertions.assertTrue(genres.contains(getSecondGenre()));
    }

    @Test
    @DisplayName("Тест получения идентификаторов жанров по идентификатору фильма")
    void getGenresIdByFilmId_ReturnCorrectFilms() {

        // when
        Set<Integer> genresIds = genreStorage.getGenresIdByFilmId(TEST_FILM_ID);

        // then
        Assertions.assertEquals(genresIds.size(), TEST_FILM_COUNT_GENRES);
        Assertions.assertTrue(genresIds.contains(FIRST_GENRE_ID));
        Assertions.assertTrue(genresIds.contains(SECOND_GENRE_ID));
    }

    @Test
    @DisplayName("Тест получения жанров по набору идентификаторов")
    void getByIds_GenresExists_ReturnCorrectGenres() {

        // init
        Set<Integer> ids = Set.of(1, 2);

        // when
        Set<Genre> genres = genreStorage.getByIds(ids);

        // then
        Assertions.assertEquals(genres.size(), 2);
        Assertions.assertTrue(genres.contains(getFirstGenre()));
        Assertions.assertTrue(genres.contains(getSecondGenre()));
    }
}