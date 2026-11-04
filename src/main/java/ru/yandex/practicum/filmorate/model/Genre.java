package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Класс для хранения информации о жанре фильма
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {

    // Поле для хранения идентификатора жанра
    private int id;

    // Поле для хранения названия жанра
    private String name;
}
