package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {
    UserController userController;
    User user;
    MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        user = createUserChosya();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("Проверка на добавление пользователя")
    void shouldAddUser() {
        userController.userCreate(user);
        assertEquals(user.getId(), 1, "Пользователь не добавлен");
    }

    @Test
    @DisplayName("Проверка на пустой id у пользователя")
    void shouldIUserIsEmpty() {
        user.setId(null);

        assertThrows(NotFoundException.class, () -> {
            userController.userUpdate(user);
        });
    }

    @Test
    @DisplayName("Проверка на обновление пользователя")
    void shouldUpdateUser() {
        userController.userCreate(user);
        User userUpdate = createUserChycha();
        userUpdate.setId(1L);
        userController.userUpdate(userUpdate);

        assertEquals(userUpdate.getEmail(), "Newexample@yandex.ru", "Пользователь не обновлен");
        assertEquals(userController.getAllUsers().size(), 1, "Количество пользователей не совпадает");
    }

    @Test
    @DisplayName("Проверка получение списка пользователей")
    void shouldGetAllUsers() {
        userController.userCreate(user);
        userController.userCreate(createUserChycha());
        userController.userCreate(createUserBocha());

        assertEquals(userController.getAllUsers().size(), 3, "Количество пользователей не совпадает");
    }

    @Test
    @DisplayName("Проверка на валидацию пользоваетеля, с пустым имейлом")
    void shouldValidationUserForEmptyEmail() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\",\"birthday\":\"2020-07-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию пользоваетеля, с имейлом без символа @")
    void shouldValidationUserWithEmailWithoutSymbol() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"exampleyandex.ru\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\",\"birthday\":\"2020-07-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию пользователя с пустым логином")
    void shouldValidationUserForEmptyLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"example@yandex.ru\","
                                + "\"login\":\"\","
                                + "\"name\":\"Chosik\",\"birthday\":\"2020-07-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию пользователя с логином, состоящим из пробелов")
    void shouldValidationUserForLoginConsistingOfSpaces() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"example@yandex.ru\","
                                + "\"login\":\"       \","
                                + "\"name\":\"Chosik\",\"birthday\":\"2020-07-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию логина с пробелами")
    void shouldValidationForUserLoginWithSpaces() {
        user.setLogin("Cho sya");

        assertThrows(ValidationException.class, () -> {
            userController.userCreate(user);
        });
    }

    @Test
    @DisplayName("Проверка на валидацию имени пользователя")
    void shouldValidationForUserName() {
        user.setName("");
        userController.userCreate(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    @DisplayName("Проверка на валидацию дня рождения пользователя")
    void shouldValidationTheBirthdayUser() {
        user.setBirthday(LocalDate.of(2050, 7, 1));

        assertThrows(ValidationException.class, () -> {
            userController.userCreate(user);
        });
    }

    private User createUserChosya() {
        User user = new User();
        user.setEmail("example@yandex.ru");
        user.setLogin("Chosya");
        user.setName("Chosik");
        user.setBirthday(LocalDate.of(2020, 7, 1));
        return user;
    }

    private User createUserChycha() {
        User user = new User();
        user.setEmail("Newexample@yandex.ru");
        user.setLogin("Chycha");
        user.setName("Chychhela");
        user.setBirthday(LocalDate.of(2020, 1, 7));
        return user;
    }

    private User createUserBocha() {
        User user = new User();
        user.setEmail("2Newexample@yandex2.ru");
        user.setLogin("Bocha");
        user.setName("Bochka");
        user.setBirthday(LocalDate.of(2015, 4, 10));
        return user;
    }
}
