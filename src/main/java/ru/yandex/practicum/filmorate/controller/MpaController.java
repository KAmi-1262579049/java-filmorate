package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

// Контроллер для обработки HTTP-запросов, связанных с рейтингами MPA
@RestController
@RequestMapping("/mpa")
public class MpaController {

    // Поле для хранения экземпляра хранилища рейтингов MPA
    private final MpaDbStorage storage;

    // Конструктор для внедрения зависимости хранилища рейтингов MPA
    public MpaController(MpaDbStorage storage) {
        this.storage = storage;
    }

    // Метод для получения списка всех рейтингов MPA
    @GetMapping
    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    // Метод для получения рейтинга MPA по его идентификатору
    @GetMapping("/{id}")
    public Mpa getById(@PathVariable int id) {
        return storage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + id + " не найден"));
    }
}
