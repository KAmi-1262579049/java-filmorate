
package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

// Класс для получения информации о жанрах из базы данных
@Repository
public class GenreDbStorage {

    // Поле для выполнения SQL-запросов с помощью JdbcTemplate
    private final JdbcTemplate jdbc;

    // Конструктор класса для внедрения зависимости JdbcTemplate
    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Метод для получения списка всех жанров из базы данных
    public List<Genre> findAll() {

        // Выполняем SQL-запрос для получения жанров, отсортированных по идентификатору
        return jdbc.query(
                "SELECT id, name FROM genres ORDER BY id",
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                )
        );
    }

    // Метод для поиска жанра по его идентификатору
    public Optional<Genre> getById(int id) {

        // Выполняем SQL-запрос для поиска жанра по идентификатору
        return jdbc.query(
                        "SELECT id, name FROM genres WHERE id = ?",
                        (rs, rowNum) -> new Genre(
                                rs.getInt("id"),
                                rs.getString("name")
                        ),
                        id
                )
                .stream()
                .findFirst();
    }
}
