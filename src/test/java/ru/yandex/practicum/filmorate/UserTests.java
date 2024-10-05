//package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

//public class UserTests {
//    private UserController userController;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        userController = new UserController();
//        user = User.builder()
//                .email("test@example.com")
//                .login("testlogin")
//                .name("Test Name")
//                .birthday(LocalDate.of(1990, 1, 1))
//                .build();
//    }
//
//    @Test
//    void shouldAddUser() {
//
//
//        User addedUser = userController.putTheUser(user);  // Обновленный метод
//
//        assertEquals(user, addedUser);
//        assertEquals(1, userController.getAllUsers().size());
//    }
//
//    @Test
//    void shouldUpdateUser() {
//
//        User addedUser = userController.putTheUser(user);
//
//        User updatedUser = User.builder()
//                .email("updated@example.com")
//                .login("updatedlogin")
//                .name("Updated Name")
//                .birthday(LocalDate.of(1990, 1, 1))
//                .build();
//        updatedUser.setId(addedUser.getId());
//
//        User refreshedUser = userController.refreshTheUser(updatedUser);
//
//        assertEquals("updated@example.com", refreshedUser.getEmail());
//        assertEquals("updatedlogin", refreshedUser.getLogin());
//        assertEquals("Updated Name", refreshedUser.getName());
//    }
//
//}
