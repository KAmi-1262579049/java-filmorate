package ru.yandex.practicum.filmorate.storage.film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;
@Repository
// Класс для получения информации о жанрах из базы данных
public class GenreDbStorage {
    // Поле для выполнения SQL-запросов с помощью JdbcTemplate
    private final JdbcTemplate jdbc;

    // Конструктор класса
    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc=jdbc;
    }
    
    // Метод для получения списка всех жанров из базы данных
    public List<Genre> findAll() {
        return jdbc.query("SELECT id,name FROM genres ORDER BY id",(rs,n)->new Genre(rs.getInt(1),rs.getString(2)));
    }
    // Метод для поиска жанра по его идентификатору
    public Optional<Genre> getById(int id) {
        return jdbc.query("SELECT id,name FROM genres WHERE id=?",(rs,n)->new Genre(rs.getInt(1),rs.getString(2)),id).stream().findFirst();
    }
}
