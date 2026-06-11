package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

// Интерфейс FilmStorage описывает операции хранения фильмов
public interface FilmStorage {

    // Добавляет новый фильм в хранилище
    Film create(Film film);

    // Обновляет уже существующий фильм
    Film update(Film film);

    // Удаляет фильм по id
    void delete(int id);

    // Возвращает фильм по id
    Film getById(int id);

    // Возвращает все фильмы
    Collection<Film> findAll();
}
