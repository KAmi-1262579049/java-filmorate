package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import java.util.List;

// Сервис для получения жанров и проверки их существования
@Service
public class GenreService {
    private final GenreStorage storage;

    // Конструктор для внедрения хранилища через интерфейс
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    // Возвращает все записи
    public List<Genre> findAll() {
        return storage.findAll();
    }

    // Возвращает запись по идентификатору или ошибку 404
    public Genre getById(int id) {
        return storage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }
}
