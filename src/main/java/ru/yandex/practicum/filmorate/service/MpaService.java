package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;
import java.util.List;

// Сервис для получения рейтингов MPA и проверки их существования
@Service
public class MpaService {
    private final MpaStorage storage;

    // Конструктор для внедрения хранилища через интерфейс
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    // Возвращает все записи
    public List<Mpa> findAll() {
        return storage.findAll();
    }

    // Возвращает запись по идентификатору или ошибку 404
    public Mpa getById(int id) {
        return storage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id=" + id + " не найден"));
    }
}
