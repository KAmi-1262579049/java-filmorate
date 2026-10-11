package ru.yandex.practicum.filmorate.storage.film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;
@Repository
// Класс для работы с рейтингами MPA в базе данных
public class MpaDbStorage {
    // Поле для выполнения SQL-запросов с помощью JdbcTemplate
    private final JdbcTemplate jdbc;
    
    // Конструктор класса для внедрения зависимости JdbcTemplate
    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc=jdbc;
    }
    
    // Метод для получения списка всех рейтингов MPA из базы данных
    public List<Mpa> findAll() {
        return jdbc.query("SELECT id,name FROM mpa ORDER BY id",(rs,n)->new Mpa(rs.getInt(1),rs.getString(2)));
    }
    
    // Метод для получения рейтинга MPA по его идентификатору
    public Optional<Mpa> getById(int id) {
        return jdbc.query("SELECT id,name FROM mpa WHERE id=?",(rs,n)->new Mpa(rs.getInt(1),rs.getString(2)),id).stream().findFirst();
    }
}
