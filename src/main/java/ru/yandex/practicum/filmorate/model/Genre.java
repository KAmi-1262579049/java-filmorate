package ru.yandex.practicum.filmorate.model;

import lombok.Data;

// Класс для хранения информации о жанре фильма
@Data
public class Genre {

    // Поле для хранения идентификатора жанра
    private int id;

    // Поле для хранения названия жанра
    private String name;
}
