package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

// Интерфейс для получения жанров из хранилища
public interface GenreStorage {
    List<Genre> findAll();
    Optional<Genre> getById(int id);
}
