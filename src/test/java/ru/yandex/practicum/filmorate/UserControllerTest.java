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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {
    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    public void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = createUserChosya();
    }

    @Test
    @DisplayName("Проверка на добавление пользователя")
    void shouldAddUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"example@yandex.ru\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\","
                                + "\"birthday\":\"2020-07-01\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Проверка на валидацию пользователя с пустым имейлом")
    void shouldValidationUserForEmptyEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\","
                                + "\"birthday\":\"2020-07-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию пользователя с некорректным имейлом")
    void shouldValidationUserWithEmailWithoutSymbol() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"exampleyandex.ru\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\","
                                + "\"birthday\":\"2020-07-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию логина с пробелами")
    void shouldValidationForUserLoginWithSpaces() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"example@yandex.ru\","
                                + "\"login\":\"Cho sya\","
                                + "\"name\":\"Chosik\","
                                + "\"birthday\":\"2020-07-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на валидацию дня рождения пользователя")
    void shouldValidationTheBirthdayUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"example@yandex.ru\","
                                + "\"login\":\"Chosya\","
                                + "\"name\":\"Chosik\","
                                + "\"birthday\":\"2050-07-01\"}"))
                .andExpect(status().isBadRequest());
    }

    private User createUserChosya() {
        User user = new User();
        user.setEmail("example@yandex.ru");
        user.setLogin("Chosya");
        user.setName("Chosik");
        user.setBirthday(LocalDate.of(2020, 7, 1));
        return user;
    }
}
