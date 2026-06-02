package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
// Класс InMemoryFilmStorage хранит фильмы в памяти приложения
public class InMemoryFilmStorage implements FilmStorage {

    // Минимально допустимая дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    // Максимальная длина описания фильма
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    // Коллекция фильмов, где ключ - id фильма
    private final Map<Integer, Film> films = new HashMap<>();
    // Счётчик для генерации уникальных id
    private int currentId = 0;

    @Override
    // Создаёт новый фильм и сохраняет его в памяти
    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    // Обновляет фильм, если фильм с таким id существует
    public Film update(Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        Film oldFilm = films.get(film.getId());
        film.getLikes().addAll(oldFilm.getLikes());
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    // Удаляет фильм по id
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        films.remove(id);
        log.info("Удалён фильм с id={}", id);
    }

    @Override
    // Возвращает фильм по id
    public Film getById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    @Override
    // Возвращает список всех фильмов
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    // Генерирует следующий уникальный id
    private int getNextId() {
        return ++currentId;
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
