package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportResource
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStorageTest {
    private final UserStorage userStorage;

    @Autowired
    public UserStorageTest(@Qualifier("jdbcUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    @Order(1)
    void shouldGetUserByIdTest() {
        Optional<User> userOptional = userStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(user).hasFieldOrPropertyWithValue("login", "Sylya");
                            assertThat(user).hasFieldOrPropertyWithValue("name", "Sylik");
                            assertThat(user).hasFieldOrPropertyWithValue("email", "sylik@mail.ru");
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    LocalDate.of(2014, 7, 7));
                        }
                );
    }

    @Test
    @Order(2)
    void shouldGetAllUsersTest() {
        Optional<Collection<User>> userList = Optional.ofNullable(userStorage.getAllUsers());
        assertThat(userList)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).isNotEmpty();
                    assertThat(user).hasSize(4);
                    assertThat(user).element(0).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).element(1).hasFieldOrPropertyWithValue("id", 2);
                    assertThat(user).element(2).hasFieldOrPropertyWithValue("id", 3);
                    assertThat(user).element(3).hasFieldOrPropertyWithValue("id", 4);
                });
    }

    @Test
    @Order(3)
    void shouldCreateUserTest() {
        User newUser = new User();
        newUser.setLogin("manul");
        newUser.setName("manulik");
        newUser.setEmail("manul@yangex.com");
        newUser.setBirthday(LocalDate.of(2020, 8, 19));

        Optional<User> userOptional = Optional.ofNullable(userStorage.userCreate(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", 5);
                            assertThat(user).hasFieldOrPropertyWithValue("login", "manul");
                            assertThat(user).hasFieldOrPropertyWithValue("name", "manulik");
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    "manul@yangex.com");
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    LocalDate.of(2020, 8, 19));
                        }
                );
    }

    @Test
    @Order(4)
    void shouldUpdateUserTest() {
        User newUser = new User();
        newUser.setId(5);
        newUser.setLogin("manul");
        newUser.setName("manulik");
        newUser.setEmail("manul@yangex.com");
        newUser.setBirthday(LocalDate.of(2020, 8, 19));

        Optional<User> userOptional = Optional.ofNullable(userStorage.userUpdate(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", 5);
                            assertThat(user).hasFieldOrPropertyWithValue("login", "manul");
                            assertThat(user).hasFieldOrPropertyWithValue("name", "manulik");
                            assertThat(user).hasFieldOrPropertyWithValue("email",
                                    "manul@yangex.com");
                            assertThat(user).hasFieldOrPropertyWithValue("birthday",
                                    LocalDate.of(2020, 8, 19));
                        }
                );
    }

    @Test
    @Order(5)
    void shouldGetAllFriendsTest() {
        Optional<List<User>> usersOptional = Optional.ofNullable(userStorage.getAllFriends(1));
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(users -> {
                            assertThat(users).isNotEmpty();
                            assertThat(users).hasSize(2);
                            assertThat(users).first().hasFieldOrPropertyWithValue("id", 2);
                            assertThat(users).element(1).hasFieldOrPropertyWithValue("id", 3);
                        }
                );
    }

    @Test
    @Order(6)
    void shouldGetCommonFriendsTest() {
        Optional<List<User>> commonFriendsOptional = Optional
                .ofNullable(userStorage.getCommonFriends(2, 3));
        assertThat(commonFriendsOptional)
                .isPresent()
                .hasValueSatisfying(users -> {
                            assertThat(users).isNotEmpty();
                            assertThat(users).hasSize(2);
                            assertThat(users).element(0).hasFieldOrPropertyWithValue("id", 1);
                            assertThat(users).element(1).hasFieldOrPropertyWithValue("id", 4);
                        }
                );
    }
}
