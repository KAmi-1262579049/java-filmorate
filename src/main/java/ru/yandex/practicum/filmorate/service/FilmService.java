package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
// Класс FilmService содержит бизнес-логику фильмов и лайков
public class FilmService {

    // Минимально допустимая дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    // Максимальная длина описания фильма
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    // Хранилище фильмов, внедряемое через интерфейс
    private final FilmStorage filmStorage;

    // Хранилище пользователей нужно для проверки, что лайк ставит существующий пользователь
    private final UserStorage userStorage;

    @Autowired
    // Конструктор получает зависимости из Spring-контекста
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Создаёт фильм
    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    // Обновляет фильм
    public Film update(Film film) {
        validate(film);
        getById(film.getId());
        return filmStorage.update(film);
    }

    // Возвращает фильм по id
    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    // Возвращает все фильмы
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    // Удаляет фильм
    public void delete(int id) {
        getById(id);
        filmStorage.delete(id);
    }

    // Добавляет лайк фильму от пользователя
    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);
        checkUserExists(userId);
        film.getLikes().add(userId);
        log.info("Пользователь с id={} поставил лайк фильму с id={}", userId, filmId);
    }

    // Удаляет лайк пользователя у фильма
    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);
        checkUserExists(userId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
    }

    // Возвращает список популярных фильмов
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество популярных фильмов должно быть положительным числом");
        }
        return filmStorage.getPopularFilms(count);
    }

    // Проверяет существование пользователя
    private void checkUserExists(int userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    // Проверяет корректность данных фильма
    private void validate(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не передан");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Описание фильма слишком длинное");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза фильма раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
