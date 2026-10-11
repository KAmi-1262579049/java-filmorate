package ru.yandex.practicum.filmorate.model;

import lombok.Data;

// Класс для хранения информации о возрастном рейтинге фильма
@Data
public class Mpa {

    // Поле для хранения идентификатора рейтинга
    private int id;

    // Поле для хранения названия рейтинга
    private String name;
}
