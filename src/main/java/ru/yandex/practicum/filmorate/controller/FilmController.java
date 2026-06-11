package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
// Класс FilmController обрабатывает HTTP-запросы, связанные с фильмами
public class FilmController {

    // Сервис фильмов, содержащий бизнес-логику
    private final FilmService filmService;

    @Autowired
    // Конструктор получает FilmService из Spring-контекста
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Обрабатывает POST-запрос на создание фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    // Обрабатывает PUT-запрос на обновление фильма
    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    // Обрабатывает GET-запрос на получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    // Обрабатывает GET-запрос на получение фильма по id
    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    // Обрабатывает PUT-запрос на добавление лайка фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    // Обрабатывает DELETE-запрос на удаление лайка у фильма
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    // Обрабатывает GET-запрос на получение популярных фильмов
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
