package ru.yandex.practicum.filmorate.storage.user.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserExtractor implements ResultSetExtractor<User> {
    @Override
    public User extractData(final ResultSet rs) throws SQLException, DataAccessException {
        User user = null;
        while (rs.next()) {
            user = new User();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
        }
        return user;
    }
}