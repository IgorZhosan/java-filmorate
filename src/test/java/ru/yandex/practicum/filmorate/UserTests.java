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
        // Создаем пользователя
        User user = new User("test@example.com", "testlogin", "Test Name", LocalDate.of(1990, 1, 1));
        User addedUser = userController.putTheUser(user);

        // Создаем обновленного пользователя с тем же id
        User updatedUser = new User("updated@example.com", "updatedlogin", "Updated Name", LocalDate.of(1990, 1, 1));
        updatedUser.setId(addedUser.getId()); // Присваиваем id ранее добавленного пользователя

        // Обновляем пользователя
        User refreshedUser = userController.refreshTheUser(updatedUser);

        // Проверяем, что обновленные поля соответствуют ожидаемым значениям
        assertEquals("updated@example.com", refreshedUser.getEmail());
        assertEquals("updatedlogin", refreshedUser.getLogin());
        assertEquals("Updated Name", refreshedUser.getName());
    }

}
