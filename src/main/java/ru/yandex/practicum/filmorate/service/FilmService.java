package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
// Класс FilmService содержит бизнес-логику фильмов и лайков
public class FilmService {

    // Хранилище фильмов, внедряемое через интерфейс.
    private final FilmStorage filmStorage;
    // Хранилище пользователей нужно для проверки существования пользователя при лайке
    private final UserStorage userStorage;

    @Autowired
    // Конструктор получает зависимости FilmStorage и UserStorage из Spring-контекста
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Создаёт фильм
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    // Обновляет фильм
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    // Возвращает фильм по id
    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    // Возвращает все фильмы
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    // Добавляет лайк фильму от пользователя
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        film.getLikes().add(userId);
        log.info("Пользователь с id={} поставил лайк фильму с id={}", userId, filmId);
    }

    // Удаляет лайк пользователя у фильма
    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
    }

    // Возвращает список популярных фильмов, отсортированных по количеству лайков
    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
