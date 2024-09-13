package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.exception.InvalidUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        userController = new UserController();
    }

    @Test
    void shouldGetAllFilms() {
        Exception exception = assertThrows(InvalidFilmException.class, () -> {
            filmController.getAllFilms();
        });
        assertEquals("Список фильмов пуст", exception.getMessage());
    }

    @Test
    void shouldAddFilm() {
        Film film = new Film(1, "Film Name", "Description");

        Film addedFilm = filmController.putTheFilm(film);
        assertEquals(film, addedFilm);
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film(1, "Film Name", "Description");
        filmController.putTheFilm(film);

        Film updatedFilm = new Film(1, "Updated Name", "Updated Description");

        Film refreshedFilm = filmController.refreshAddToFilm(updatedFilm);

        assertEquals("Updated Name", refreshedFilm.getName());
        assertEquals("Updated Description", refreshedFilm.getDescription());
    }

    @Test
    void shouldGetAllUsers() {
        Exception exception = assertThrows(InvalidUserException.class, () -> {
            userController.getAllUsers();
        });
        assertEquals("Список пользователей пуст", exception.getMessage());
    }

    @Test
    void shouldAddUser() {
        User user = new User(1, "test@example.com", "testlogin", "Test Name", new Date());

        User addedUser = userController.putTheUser(user);
        assertEquals(user, addedUser);
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void shouldUpdateUser() {
        // Создание и добавление пользователя
        User user = new User(1, "test@example.com", "testlogin", "Test Name", new Date());
        userController.putTheUser(user);

        User updatedUser = new User(1, "updated@example.com", "updatedlogin", "Updated Name", new Date());

        User refreshedUser = userController.refreshTheUser(updatedUser);

        assertEquals("updated@example.com", refreshedUser.getEmail());
        assertEquals("updatedlogin", refreshedUser.getLogin());
        assertEquals("Updated Name", refreshedUser.getName());
    }
}
