package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.extractor.FilmsExtractor;
import ru.yandex.practicum.filmorate.storage.user.extractor.UserExtractor;
import ru.yandex.practicum.filmorate.storage.user.extractor.UsersExtractor;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcUserStorage implements UserStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final UserExtractor userExtractor;
    private final UsersExtractor usersExtractor;
    private final FilmsExtractor filmsExtractor;

    @Override //получение списка пользователей.
    public Collection<User> getAllUsers() {
        String sql = "SELECT * " +
                "FROM users u; ";

        return jdbc.query(sql, usersExtractor);
    }

    @Override // для добавления нового пользователя в список.
    public User userCreate(final User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (:email, :login, :name, :birthday); ";
        Map<String, Object> params = new HashMap<>();

        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("birthday", user.getBirthday());

        jdbc.update(sql, new MapSqlParameterSource().addValues(params), keyHolder, new String[]{"user_id"});
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return user;
    }

    @Override //для обновления данных существующего пользователя.
    public User userUpdate(final User user) {
        String sql = "UPDATE users SET login = :login, " +
                "name = :name, " +
                "email = :email, " +
                "birthday = :birthday " +
                "WHERE user_id = :user_id; ";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user.getId());
        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("birthday", user.getBirthday());
        jdbc.update(sql, params);

        return user;
    }

    @Override // получение пользователя по id
    public Optional<User> getUserById(final int id) {
        String sql = "SELECT * " +
                "FROM users AS u " +
                "LEFT JOIN friends f ON u.user_id = f.user_id " +
                "WHERE u.user_id = :user_id;";
        User user = jdbc.query(sql, Map.of("user_id", id), userExtractor);

        return Optional.ofNullable(user);
    }

    @Override
    public void addNewFriend(final int userId, final int friendId) { //добавление пользователя в друзья
        String sql = "MERGE INTO friends (user_id, friend_id) " +
                "VALUES (:user_id, :friend_id); ";
        jdbc.update(sql, Map.of("user_id", userId, "friend_id", friendId));
    }

    @Override
    public void deleteFriend(final int userId, final int friendId) { // удаление из друзей пользователя
        String sql = "DELETE FROM friends " +
                "WHERE user_id = :user_id AND friend_id = :friend_id; ";
        jdbc.update(sql, Map.of("user_id", userId, "friend_id", friendId));
    }

    @Override
    public List<User> getAllFriends(final int userId) { // получение списка друзей пользователя
        String sql = "SELECT * " +
                "FROM users u " +
                "WHERE user_id IN (SELECT friend_id " +
                "FROM friends " +
                "WHERE user_id = :user_id); ";

        return jdbc.query(sql, Map.of("user_id", userId), usersExtractor);
    }

    @Override
    public List<User> getCommonFriends(final int userId, final int otherId) { // получение списка общих друзей с пользователем
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT f.friend_id " +
                "FROM users AS u " +
                "LEFT JOIN friends AS f ON u.user_id = f.user_id " +
                "WHERE u.user_id = :user_id AND f.friend_id IN (SELECT fr.friend_id " +
                "FROM users AS us " +
                "LEFT JOIN friends fr ON us.user_id = fr.user_id " +
                "WHERE us.user_id = :other_id));";

        return jdbc.query(sql, Map.of("user_id", userId, "other_id", otherId), usersExtractor);
    }

    @Override
    public Set<Film> getRecommendations(int userId) {
        String sql = """
        SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, g.genre_id, g.genre_name
        FROM films f
        JOIN likes l ON f.film_id = l.film_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
        WHERE l.user_id IN (
            SELECT other_l.user_id
            FROM likes user_l
            JOIN likes other_l ON user_l.film_id = other_l.film_id
            WHERE user_l.user_id = :userId AND other_l.user_id != :userId
            GROUP BY other_l.user_id
            HAVING COUNT(DISTINCT other_l.film_id) > 0
        )
        AND f.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = :userId)
        GROUP BY f.film_id;
    """;

        Map<String, Object> params = Map.of("userId", userId);
        Map<Integer, Film> filmsMap = jdbc.query(sql, params, filmsExtractor);

        return new HashSet<>(filmsMap.values());
    }
}
