package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;

// Интерфейс для получения рейтингов MPA из хранилища
public interface MpaStorage {
    List<Mpa> findAll();
    Optional<Mpa> getById(int id);
}
