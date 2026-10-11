package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Класс Genre для хранения информации о жанре фильма
public class Genre {
    // Поле для хранения уникального идентификатора жанра
    private int id;
    // Поле для хранения названия жанра
    private String name;
}
