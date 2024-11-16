package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.base.BaseStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserStorage extends BaseStorage<User> implements UserRepository {

    private final FilmStorage filmDbRepository;

    public UserStorage(NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper, FilmStorage filmDbRepository) {
        super(jdbc, mapper);
        this.filmDbRepository = filmDbRepository;
    }

    private static final String SQL_GET_ALL_USERS =
            "SELECT * FROM users;";

    private static final String SQL_GET_USER_BY_ID =
            "SELECT * FROM users WHERE user_id=:id;";

    private static final String SQL_INSERT_USER =
            "INSERT INTO users (email, login, user_name, birthday) " +
                    "VALUES (:email, :login, :user_name, :birthday);";

    private static final String SQL_GET_USER_FRIENDS =
            "SELECT * FROM users WHERE user_id IN " +
                    "(SELECT friend_id FROM users_friends WHERE user_id=:user_id);";

    private static final String SQL_INSERT_USER_FRIEND =
            "INSERT INTO users_friends (user_id, friend_id) " +
                    "VALUES (:user_id, :friend_id);";

    private static final String SQL_DELETE_USER_FRIEND =
            "DELETE FROM users_friends WHERE user_id=:user_id AND friend_id=:friend_id;";

    private static final String SQL_UPDATE_USER =
            "UPDATE users SET email=:email, login=:login, user_name=:user_name, " +
                    "birthday=:birthday WHERE user_id=:user_id;";

    private static final String SQL_DELETE_USER =
            "DELETE FROM users WHERE user_id=:user_id";

    private static final String SQL_GET_COMMON_USER =
            "SELECT * FROM users WHERE user_id IN (SELECT uf1.friend_id FROM users_friends AS uf1 " +
                    "INNER JOIN users_friends AS uf2 ON uf1.FRIEND_ID = uf2.FRIEND_ID " +
                    "WHERE uf1.user_id=:id AND uf2.user_id=:other_id);";

    @Override
    public List<User> getAll() {
        return getMany(SQL_GET_ALL_USERS);
    }

    @Override
    public Optional<User> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        return getOne(SQL_GET_USER_BY_ID, params);
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return getMany(SQL_GET_USER_FRIENDS, Map.of("user_id", id));
    }

    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("user_name", user.getName());
        params.addValue("birthday", user.getBirthday());
        jdbc.update(SQL_INSERT_USER, params, keyHolder);
        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);
        jdbc.update(SQL_INSERT_USER_FRIEND, params);
    }

    @Override
    public User updateUser(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user.getId());
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("user_name", user.getName());
        params.addValue("birthday", user.getBirthday());
        jdbc.update(SQL_UPDATE_USER, params);
        return getById(user.getId()).get();
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbc.update(SQL_DELETE_USER_FRIEND, Map.of("user_id", userId, "friend_id", friendId));
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        return new LinkedHashSet<>(getMany(SQL_GET_COMMON_USER, Map.of("id", id, "other_id", otherId)));
    }

    @Override
    public Boolean deleteUser(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", id);
        int res = jdbc.update(SQL_DELETE_USER, params);
        return res == 1;
    }

    public List<Integer> getIdFilmsLikesByUser(Integer userId) {
        String sql = "SELECT film_id FROM users_films_likes WHERE user_id = :user_id;";
        return jdbc.queryForList(sql, Map.of("user_id", userId), Integer.class);
    }

    public Set<Film> getRecommendations(Integer userId) {
        Map<Integer, List<Integer>> userIdFilmsLikes = new HashMap<>();
        List<User> users = this.getAll();

        for (User user : users) {
            userIdFilmsLikes.put(user.getId(), this.getIdFilmsLikesByUser(user.getId()));
        }

        long maxCount = 0;
        Set<Integer> overlapUserIds = new HashSet<>();
        for (Integer id : userIdFilmsLikes.keySet()) {
            if (id.equals(userId)) continue;
            Integer overlapCount = (int) userIdFilmsLikes.get(id).stream()
                    .filter(filmId -> userIdFilmsLikes.get(userId).contains(filmId)).count();
            if (overlapCount == maxCount && overlapCount != 0) {
                overlapUserIds.add(id);
            }
            if (overlapCount > maxCount) {
                maxCount = overlapCount;
                overlapUserIds = new HashSet<>();
                overlapUserIds.add(id);
            }
        }

        return overlapUserIds.stream()
                .flatMap(idUser -> userIdFilmsLikes.get(idUser).stream())
                .filter(filmId -> !userIdFilmsLikes.get(userId).contains(filmId))
                .map(filmId -> filmDbRepository.getById(filmId).orElseThrow())
                .collect(Collectors.toSet());
    }
}