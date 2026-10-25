package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("userDbStorage")
// Класс реализует интерфейс UserStorage для работы с пользователями в базе данных
public class UserDbStorage implements UserStorage {
    // Поле для выполнения SQL-запросов через JdbcTemplate
    private final JdbcTemplate jdbc;
    // Поле для преобразования строки результата SQL-запроса в объект User
    private final RowMapper<User> mapper = (rs, row) -> {
        User user = new User();
        user.setId(rs.getInt("id")); user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login")); user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    // Конструктор класса
    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    // Метод для создания нового пользователя в базе данных
    public User create(User user) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users(email,login,name,birthday) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,user.getEmail()); ps.setString(2,user.getLogin()); ps.setString(3,user.getName());
            ps.setDate(4,java.sql.Date.valueOf(user.getBirthday())); return ps;
        }, holder);
        user.setId(holder.getKey().intValue()); return user;
    }

    @Override
    // Метод для обновления существующего пользователя в базе данных
    public User update(User user) {
        jdbc.update("UPDATE users SET email=?,login=?,name=?,birthday=? WHERE id=?",user.getEmail(),user.getLogin(),user.getName(),user.getBirthday(),user.getId());
        return user;
    }

    @Override
    // Метод для удаления пользователя из базы данных по идентификатору
    public void delete(int id) {
        jdbc.update("DELETE FROM users WHERE id=?",id);
    }

    @Override
    // Метод для поиска пользователя по идентификатору с возвратом Optional
    public Optional<User> getById(int id) {
        return jdbc.query("SELECT * FROM users WHERE id=?",mapper,id).stream().findFirst();
    }

    @Override
    // Метод для получения коллекции всех пользователей
    public Collection<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY id",mapper);
    }

    // Метод для добавления другого пользователя в список друзей
    public void addFriend(int id,int friendId) {
        jdbc.update("MERGE INTO friendships(user_id,friend_id,confirmed) KEY(user_id,friend_id) VALUES(?,?,FALSE)",id,friendId);
    }

    // Метод для удаления пользователя из списка друзей
    public void removeFriend(int id,int friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?",id,friendId);
    }

    // Метод для получения списка друзей указанного пользователя
    public List<User> getFriends(int id) {
        return jdbc.query("SELECT u.* FROM users u JOIN friendships f ON f.friend_id=u.id WHERE f.user_id=? ORDER BY u.id",mapper,id);
    }

    // Метод для получения общих друзей двух пользователей
    public List<User> getCommonFriends(int id,int other) {
        return jdbc.query("SELECT u.* FROM users u JOIN friendships a ON a.friend_id=u.id JOIN friendships b ON b.friend_id=u.id WHERE a.user_id=? AND b.user_id=? ORDER BY u.id",mapper,id,other);
    }
}
