//package user;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.context.annotation.Import;
//import ru.yandex.practicum.filmorate.model.User;
//
//import ru.yandex.practicum.filmorate.storage.director.DirectorRowMapper;
//import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
//import ru.yandex.practicum.filmorate.storage.film.*;
//import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
//import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
//import ru.yandex.practicum.filmorate.storage.mpa.MpaRowMapper;
//import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
//import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;
//import ru.yandex.practicum.filmorate.storage.user.UserStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@JdbcTest
//@Import({UserStorage.class, UserRowMapper.class,
//        FilmStorage.class, FilmExtractor.class, FilmGenreRowMapper.class, FilmRowMapper.class,
//        GenreStorage.class, GenreRowMapper.class, MpaStorage.class, MpaRowMapper.class,
//        DirectorStorage.class, DirectorRowMapper.class, FilmDirectorRowMapper.class})
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DisplayName("UserStorageTest")
//class UserStorageTest {
//
//    private final UserStorage userStorage;
//
//    public static final Integer COUNT_USERS = 3;
//    public static final Integer FIRST_USER_ID = 1;
//
//    public static User getFirstUser() {
//        return User.builder().id(FIRST_USER_ID)
//                .name("Петр")
//                .email("first@yandex.ru")
//                .login("Petro")
//                .birthday(LocalDate.of(1990, 05, 19))
//                .build();
//    }
//
//    public User getNewUser() {
//        return User.builder()
//                .name("Новый")
//                .email("new@yandex.ru")
//                .login("New")
//                .birthday(LocalDate.of(1967, 05, 19))
//                .build();
//    }
//
//    @Test
//    @DisplayName("Тест получения всех пользователей")
//    void getAll_ReturnAllUsers() {
//        // when
//        List<User> users = userStorage.getAll();
//
//        // then
//        Assertions.assertEquals(COUNT_USERS, users.size());
//        Assertions.assertEquals(getFirstUser(), users.get(0));
//    }
//
//    @Test
//    @DisplayName("Тест получения имеющегося пользователя по идентификатору")
//    void getById_UserExist_ReturnUser() {
//        // when
//        Optional<User> user = userStorage.getById(FIRST_USER_ID);
//
//        // then
//        Assertions.assertTrue(user.isPresent());
//        Assertions.assertEquals(getFirstUser(), user.get());
//    }
//
//    @Test
//    @DisplayName("Тест получения списка друзей по идентификатору пользователя")
//    void getFriendsByUserId_ReturnListFriends() {
//
//        // init
//        Optional<User> fr1 = userStorage.getById(2);
//        Optional<User> fr2 = userStorage.getById(3);
//
//        // when
//        List<User> friends = userStorage.getFriendsByUserId(FIRST_USER_ID);
//
//        // then
//        Assertions.assertNotNull(friends);
//        Assertions.assertEquals(2, friends.size());
//        Assertions.assertEquals(fr1.get(), friends.get(0));
//        Assertions.assertEquals(fr2.get(), friends.get(1));
//    }
//
//    @Test
//    @DisplayName("Тест добавления нового пользователя с корректными параметрами")
//    void addUser_CorrectParams_UserAdded() {
//
//        // init
//        User newUser = getNewUser();
//
//        // when
//        newUser = userStorage.addUser(newUser);
//
//        // then
//        List<User> users = userStorage.getAll();
//        Assertions.assertEquals(COUNT_USERS + 1, users.size());
//
//        Optional<User> user = userStorage.getById(newUser.getId());
//        Assertions.assertTrue(user.isPresent());
//        Assertions.assertEquals(newUser, user.get());
//    }
//
//    @Test
//    @DisplayName("Тест добавления нового друга")
//    void addFriend_FriendAdded() {
//
//        // when
//        userStorage.addFriend(3, 1);
//
//        // then
//        List<User> friends = userStorage.getFriendsByUserId(3);
//        Assertions.assertEquals(1, friends.size());
//        Assertions.assertEquals(getFirstUser(), friends.get(0));
//    }
//
//    @Test
//    @DisplayName("Тест изменения данных пользователя")
//    void updateUser_CorrectParams_UserUpdated() {
//        // init
//        User user = userStorage.getById(FIRST_USER_ID).get();
//        String updName = "Обновленное имя";
//        String updLogin = "Обновленный логин";
//        String updEmail = "update@yandex.ru";
//        LocalDate updDate = LocalDate.of(1990, 5, 5);
//
//        // when
//        user.setName(updName);
//        user.setLogin(updLogin);
//        user.setBirthday(updDate);
//        user.setEmail(updEmail);
//        userStorage.updateUser(user);
//
//        // then
//        User updUser = userStorage.getById(FIRST_USER_ID).get();
//        Assertions.assertEquals(updName, updUser.getName());
//        Assertions.assertEquals(updLogin, updUser.getLogin());
//        Assertions.assertEquals(updEmail, updUser.getEmail());
//        Assertions.assertEquals(updDate, updUser.getBirthday());
//    }
//
//    @Test
//    @DisplayName("Тест получения общих друзей пользователя")
//    void getCommonFriends_ReturnCommon() {
//        // init
//        User commonFriend = userStorage.getById(3).get();
//
//        // when
//        Set<User> commonFriends = userStorage.getCommonFriends(1, 2);
//
//        // then
//        Assertions.assertEquals(1, commonFriends.size());
//        Assertions.assertTrue(commonFriends.contains(commonFriend));
//    }
//
//    @Test
//    @DisplayName("Удаление имеющегося в базе пользователя")
//    void deleteExistUser_UserDeleted() {
//        // when
//        Boolean res = userStorage.deleteUser(FIRST_USER_ID);
//
//        // then
//        Assertions.assertTrue(res);
//        Assertions.assertEquals(2, userStorage.getAll().size());
//        Assertions.assertFalse(userStorage.getAll().contains(getFirstUser()));
//    }
//
//    @Test
//    @DisplayName("Удаление пользователя, которого нет в базе")
//    void deleteNotExistUser_ResultFalse() {
//        // when (идентификатор не существующего пользователя)
//        Boolean res = userStorage.deleteUser(100);
//
//        // then
//        Assertions.assertFalse(res);
//        Assertions.assertEquals(3, userStorage.getAll().size());
//    }
//}