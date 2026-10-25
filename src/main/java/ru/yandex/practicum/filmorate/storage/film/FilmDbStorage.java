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
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Класс FilmDbStorage реализует интерфейс FilmStorage для работы с фильмами в базе данных
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    // SQL-запрос для получения фильмов вместе с названиями возрастных рейтингов
    private static final String SELECT = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa m ON m.id = f.mpa_id
            """;

    // Поле для выполнения SQL-запросов с помощью JdbcTemplate
    private final JdbcTemplate jdbc;

    // Поле mapper для преобразования строки ResultSet в объект Film
    private final RowMapper<Film> mapper = (rs, rowNum) -> {
        Film film = new Film();

        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Получаем идентификатор возрастного рейтинга фильма
        int mpaId = rs.getInt("mpa_id");

        // Устанавливаем возрастной рейтинг, если он указан в базе данных
        if (!rs.wasNull()) {
            film.setMpa(new Mpa(mpaId, rs.getString("mpa_name")));
        }

        return film;
    };

    // Конструктор класса для внедрения JdbcTemplate
    public FilmDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Метод для создания нового фильма в базе данных
    @Override
    @Transactional
    public Film create(Film film) {

        // Объект для получения сгенерированного идентификатора фильма
        KeyHolder holder = new GeneratedKeyHolder();

        // Выполняем SQL-запрос для добавления фильма
        jdbc.update(connection -> {

            // Подготавливаем SQL-запрос с возможностью получения сгенерированного ключа
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO films (name, description, release_date, duration, mpa_id)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            // Устанавливаем название фильма
            ps.setString(1, film.getName());

            // Устанавливаем описание фильма
            ps.setString(2, film.getDescription());

            // Устанавливаем дату выхода фильма
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));

            // Устанавливаем продолжительность фильма
            ps.setInt(4, film.getDuration());

            // Проверяем наличие возрастного рейтинга
            if (film.getMpa() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, film.getMpa().getId());
            }

            return ps;
        }, holder);

        // Получаем идентификатор созданного фильма
        Number generatedId = holder.getKey();

        // Проверяем, что база данных вернула идентификатор
        if (generatedId == null) {
            throw new IllegalStateException("Не удалось получить ID созданного фильма");
        }

        // Устанавливаем идентификатор созданному фильму
        film.setId(generatedId.intValue());

        // Сохраняем жанры фильма
        saveGenres(film);

        // Возвращаем созданный фильм вместе с жанрами и лайками
        return getById(film.getId()).orElseThrow();
    }

    // Метод для обновления существующего фильма
    @Override
    @Transactional
    public Film update(Film film) {

        // Обновляем основные данные фильма в базе данных
        jdbc.update(
                """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE id = ?
                """,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );

        // Удаляем предыдущие связи фильма с жанрами
        jdbc.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        // Сохраняем актуальные жанры фильма
        saveGenres(film);

        // Возвращаем обновлённый фильм
        return getById(film.getId()).orElseThrow();
    }

    // Приватный метод для сохранения жанров фильма в базе данных
    private void saveGenres(Film film) {

        // Проверяем, что список жанров существует
        if (film.getGenres() != null) {

            // Перебираем уникальные жанры фильма
            for (Genre genre : film.getGenres().stream().distinct().toList()) {

                // Сохраняем связь фильма с жанром без создания дубликатов
                jdbc.update(
                        """
                        MERGE INTO film_genres (film_id, genre_id)
                        KEY (film_id, genre_id)
                        VALUES (?, ?)
                        """,
                        film.getId(),
                        genre.getId()
                );
            }
        }
    }

    // Приватный метод для дополнения фильма информацией о жанрах и лайках
    private Film enrich(Film film) {

        // Получаем все жанры фильма из базы данных
        film.setGenres(
                jdbc.query(
                        """
                        SELECT g.id, g.name
                        FROM genres g
                        JOIN film_genres fg ON fg.genre_id = g.id
                        WHERE fg.film_id = ?
                        ORDER BY g.id
                        """,
                        (rs, rowNum) -> new Genre(
                                rs.getInt("id"),
                                rs.getString("name")
                        ),
                        film.getId()
                )
        );

        // Получаем идентификаторы пользователей, поставивших лайк фильму
        film.getLikes().addAll(
                jdbc.query(
                        "SELECT user_id FROM likes WHERE film_id = ?",
                        (rs, rowNum) -> rs.getInt(1),
                        film.getId()
                )
        );

        // Возвращаем дополненный объект фильма
        return film;
    }

    // Метод для удаления фильма из базы данных по ID
    @Override
    public void delete(int id) {

        // Удаляем фильм по идентификатору
        jdbc.update(
                "DELETE FROM films WHERE id = ?",
                id
        );
    }

    // Метод для поиска фильма по ID с возвращаемым типом Optional
    @Override
    public Optional<Film> getById(int id) {

        // Ищем фильм и дополняем его жанрами и лайками
        return jdbc.query(
                        SELECT + " WHERE f.id = ?",
                        mapper,
                        id
                )
                .stream()
                .map(this::enrich)
                .findFirst();
    }

    // Метод для получения коллекции всех фильмов
    @Override
    public Collection<Film> findAll() {

        // Получаем фильмы, сортируем по ID и дополняем жанрами и лайками
        return jdbc.query(
                        SELECT + " ORDER BY f.id",
                        mapper
                )
                .stream()
                .map(this::enrich)
                .toList();
    }

    // Метод для получения списка самых популярных фильмов
    @Override
    public List<Film> getPopularFilms(int count) {

        // Получаем фильмы в порядке убывания количества лайков
        return jdbc.query(
                        SELECT + """
                                LEFT JOIN likes l ON l.film_id = f.id
                                GROUP BY f.id, f.name, f.description,
                                         f.release_date, f.duration,
                                         f.mpa_id, m.name
                                ORDER BY COUNT(l.user_id) DESC, f.id
                                LIMIT ?
                                """,
                        mapper,
                        count
                )
                .stream()
                .map(this::enrich)
                .toList();
    }

    // Метод для добавления лайка фильму от конкретного пользователя
    public void addLike(int filmId, int userId) {

        // Сохраняем лайк без создания повторной записи
        jdbc.update(
                """
                MERGE INTO likes (film_id, user_id)
                KEY (film_id, user_id)
                VALUES (?, ?)
                """,
                filmId,
                userId
        );
    }

    // Метод для удаления лайка пользователя у фильма
    public void removeLike(int filmId, int userId) {

        // Удаляем связь между фильмом и пользователем
        jdbc.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId,
                userId
        );
    }
}
