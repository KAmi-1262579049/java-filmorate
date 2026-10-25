package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.Collection;

// Контроллер для обработки HTTP-запросов, связанных с жанрами фильмов
@RestController
@RequestMapping("/genres")
public class GenreController {

    // Поле для хранения экземпляра хранилища жанров
    private final GenreDbStorage genreStorage;

    // Конструктор для внедрения зависимости хранилища жанров
    public GenreController(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    // Метод для получения списка всех жанров
    @GetMapping
    public Collection<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    // Метод для получения жанра по его идентификатору
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreStorage.getById(id)
                .orElseThrow(() -> new ru.yandex.practicum.filmorate.exception.NotFoundException(
                        "Жанр с id " + id + " не найден"
                ));
    }
}
