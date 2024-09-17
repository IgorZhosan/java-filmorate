package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldAddUser() {
        User user = new User("test@example.com", "testlogin", "Test Name", LocalDate.of(1990, 1, 1));

        User addedUser = userController.putTheUser(user);  // Обновленный метод

        assertEquals(user, addedUser);
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User("test@example.com", "testlogin", "Test Name", LocalDate.of(1990, 1, 1));
        userController.putTheUser(user);

        User updatedUser = new User("updated@example.com", "updatedlogin", "Updated Name", LocalDate.of(1990, 1, 1));

        User refreshedUser = userController.refreshTheUser(updatedUser);

        assertEquals("updated@example.com", refreshedUser.getEmail());
        assertEquals("updatedlogin", refreshedUser.getLogin());
        assertEquals("Updated Name", refreshedUser.getName());
    }
}
