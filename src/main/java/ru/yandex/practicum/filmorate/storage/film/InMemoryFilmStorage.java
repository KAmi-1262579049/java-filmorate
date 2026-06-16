package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
// Класс InMemoryFilmStorage хранит фильмы в памяти приложения
public class InMemoryFilmStorage implements FilmStorage {

    // Коллекция фильмов, где ключ - id фильма
    private final Map<Integer, Film> films = new HashMap<>();

    // Счётчик для генерации уникальных id
    private int currentId = 0;

    @Override
    // Создаёт новый фильм и сохраняет его в памяти
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    // Обновляет фильм в хранилище
    public Film update(Film film) {
        Film oldFilm = films.get(film.getId());
        film.getLikes().addAll(oldFilm.getLikes());
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    // Удаляет фильм по id
    public void delete(int id) {
        films.remove(id);
        log.info("Удалён фильм с id={}", id);
    }

    @Override
    // Возвращает фильм по id
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    // Возвращает список всех фильмов
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    // Возвращает список популярных фильмов
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    // Генерирует следующий уникальный id
    private int getNextId() {
        return ++currentId;
    }
}
