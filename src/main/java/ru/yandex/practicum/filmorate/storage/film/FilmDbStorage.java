package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("filmDbStorage")
// Класс FilmDbStorage реализует интерфейс FilmStorage для работы с фильмами в базе данных
public class FilmDbStorage implements FilmStorage {
    // Поле для выполнения SQL-запросов с помощью JdbcTemplate
    private final JdbcTemplate jdbc;
    // Поле mapper для преобразования строки ResultSet в объект Film
    private final RowMapper<Film> mapper = (rs, row) -> {
        Film film = new Film();
        film.setId(rs.getInt("id")); film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description")); film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) film.setMpa(new Mpa(mpaId,rs.getString("mpa_name")));
        return film;
    };
    // SQL-запрос для получения фильмов вместе с названиями возрастных рейтингов
    private static final String SELECT = "SELECT f.*,m.name AS mpa_name FROM films f LEFT JOIN mpa m ON m.id=f.mpa_id";

    // Конструктор класса
    public FilmDbStorage(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @Override @Transactional public Film create(Film film) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO films(name,description,release_date,duration,mpa_id) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,film.getName()); ps.setString(2,film.getDescription());
            ps.setDate(3,java.sql.Date.valueOf(film.getReleaseDate())); ps.setInt(4,film.getDuration());
            if (film.getMpa()==null) ps.setNull(5,java.sql.Types.INTEGER); else ps.setInt(5,film.getMpa().getId());
            return ps;
        },holder);
        film.setId(holder.getKey().intValue()); saveGenres(film); return getById(film.getId()).orElseThrow();
    }

    @Override
    @Transactional
    // Метод для обновления существующего фильма
    public Film update(Film film) {
        jdbc.update("UPDATE films SET name=?,description=?,release_date=?,duration=?,mpa_id=? WHERE id=?",film.getName(),film.getDescription(),film.getReleaseDate(),film.getDuration(),film.getMpa()==null?null:film.getMpa().getId(),film.getId());
        jdbc.update("DELETE FROM film_genres WHERE film_id=?",film.getId()); saveGenres(film);
        return getById(film.getId()).orElseThrow();
    }

    // Приватный метод для сохранения жанров фильма в базе данных
    private void saveGenres(Film film) {
        if (film.getGenres()!=null) for (Genre genre : film.getGenres().stream().distinct().toList())
            jdbc.update("MERGE INTO film_genres(film_id,genre_id) KEY(film_id,genre_id) VALUES(?,?)",film.getId(),genre.getId());
    }

    // Приватный метод для дополнения фильма информацией о жанрах и лайках
    private Film enrich(Film film) {
        film.setGenres(jdbc.query("SELECT g.id,g.name FROM genres g JOIN film_genres fg ON fg.genre_id=g.id WHERE fg.film_id=? ORDER BY g.id",(rs,n)->new Genre(rs.getInt("id"),rs.getString("name")),film.getId()));
        film.getLikes().addAll(jdbc.query("SELECT user_id FROM likes WHERE film_id=?",(rs,n)->rs.getInt(1),film.getId()));
        return film;
    }

    @Override
    // Метод для удаления фильма из базы данных по ID
    public void delete(int id) {
        jdbc.update("DELETE FROM films WHERE id=?",id);
    }

    @Override
    // Метод для поиска фильма по ID с возвращаемым типом Optional
    public Optional<Film> getById(int id) {
        return jdbc.query(SELECT+" WHERE f.id=?",mapper,id).stream().map(this::enrich).findFirst();
    }

    @Override
    // Метод для получения коллекции всех фильмов
    public Collection<Film> findAll() {
        return jdbc.query(SELECT+" ORDER BY f.id",mapper).stream().map(this::enrich).toList();
    }

    @Override
    // Метод для получения списка самых популярных фильмов
    public List<Film> getPopularFilms(int count) {
        return jdbc.query(SELECT+" LEFT JOIN likes l ON l.film_id=f.id GROUP BY f.id,f.name,f.description,f.release_date,f.duration,f.mpa_id,m.name ORDER BY COUNT(l.user_id) DESC,f.id LIMIT ?",mapper,count).stream().map(this::enrich).toList();
    }

    // Метод для добавления лайка фильму от конкретного пользователя
    public void addLike(int filmId,int userId) {
        jdbc.update("MERGE INTO likes(film_id,user_id) KEY(film_id,user_id) VALUES(?,?)",filmId,userId);
    }

    // Метод для удаления лайка пользователя у фильма
    public void removeLike(int filmId,int userId) {
        jdbc.update("DELETE FROM likes WHERE film_id=? AND user_id=?",filmId,userId);
    }
}
