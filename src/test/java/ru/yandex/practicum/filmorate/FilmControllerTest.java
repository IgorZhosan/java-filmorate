package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FilmControllerTest {
    FilmController filmController;
    Film film;
    MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
        film = createHarryPotterAndGobletOfFire();
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    @DisplayName("Проверка на добавление фильма")
    void shouldAddFilm() {
        filmController.filmCreate(film);
        assertEquals(film.getId(), 1, "Фильм не добавлен");
    }

    @Test
    @DisplayName("Проверка на пустой id у фильма")
    void shouldIdFilmIsEmpty() {
        film.setId(null);

        assertThrows(NotFoundException.class, () -> {
            filmController.filmUpdate(film);
        });
    }

    @Test
    @DisplayName("Проверка на обновление фильма")
    void shouldUpdateFilm() {
        filmController.filmCreate(film);

        Film updatedFilm = createHarryPotterAndOrderOfPhoenix();
        updatedFilm.setId(1L);

        filmController.filmUpdate(updatedFilm);
        assertEquals(updatedFilm.getDuration(), 138, "Фильм не обновлен");
        assertEquals(filmController.getAllFilms().size(), 1, "Количество фильмов не совпадает");
    }

    @Test
    @DisplayName("Проверка получение списка фильмов")
    void shouldGetAllFilms() {
        filmController.filmCreate(film);
        filmController.filmCreate(createHarryPotterAndOrderOfPhoenix());
        filmController.filmCreate(createHarryPotterAndHalfBloodPrince());

        assertEquals(filmController.getAllFilms().size(), 3, "Количество фильмов не совпадает");
    }

    @Test
    @DisplayName("Проверка на валидацию фильма с пустым названием")
    void shouldValidationFilmForEmptyName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \","
                                + "\"description\":\"Гарри Поттер, Рон и Гермиона возвращаются на четвёртый курс школы чародейства и волшебства Хогвартс.\","
                                + "\"releaseDate\":\"2005-11-6\",\"duration\":157}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию фильма с описанием, превышающим максимально допустимую длину")
    void shouldValidationFilmForMaxLengthForDescription() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Гарри Поттер и Кубок огня.\","
                                + "\"description\":\"Гарри Поттер, Рон и Гермиона возвращаются на четвёртый курс школы " +
                                "чародейства и волшебства Хогвартс. При таинственных обстоятельствах Гарри был отобран " +
                                "в число участников опасного соревнования — Турнира Трёх Волшебников\","
                                + "\"releaseDate\":\"2005-11-6\",\"duration\":157}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Проверка на валидацию фильма с некорректной датой релиза")
    void shouldValidationFilmForIncorrectReleaseDate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Гарри Поттер и Кубок огня.\","
                                + "\"description\":\"Гарри Поттер, Рон и Гермиона возвращаются на четвёртый курс школы чародейства и волшебства Хогвартс.\","
                                + "\"releaseDate\":\"1500-11-6\",\"duration\":157}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Проверка на валидацию фильма с отрицательной продолжительностью")
    void shouldValidationFilmANegativeDuration() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Гарри Поттер и Кубок огня.\","
                                + "\"description\":\"Гарри Поттер, Рон и Гермиона возвращаются на четвёртый курс школы чародейства и волшебства Хогвартс.\","
                                + "\"releaseDate\":\"2005-11-6\",\"duration\":-1}"))
                .andExpect(status().isBadRequest());
    }

    private Film createHarryPotterAndGobletOfFire() {
        Film film = new Film();
        film.setName("Гарри Поттер и Кубок огня");
        film.setDescription("Гарри Поттер, Рон и Гермиона возвращаются на четвёртый курс школы чародейства и волшебства Хогвартс.");
        film.setReleaseDate(LocalDate.of(2005, 11, 6));
        film.setDuration(157L);
        return film;
    }

    private Film createHarryPotterAndOrderOfPhoenix() {
        Film film = new Film();
        film.setName("Гарри Поттер и Орден Феникса");
        film.setDescription("Гарри проводит свой пятый год в школе Хогвартс.");
        film.setReleaseDate(LocalDate.of(2007, 6, 28));
        film.setDuration(138L);
        return film;
    }

    private Film createHarryPotterAndHalfBloodPrince() {
        Film film = new Film();
        film.setName("Гарри Поттер и Принц-полукровка");
        film.setDescription("Теперь не только мир волшебников, но и мир маглов ощущает на себе силу Волан-де-Морта.");
        film.setReleaseDate(LocalDate.of(2009, 7, 6));
        film.setDuration(153L);
        return film;
    }
}
