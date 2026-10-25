package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Класс для хранения информации о возрастном рейтинге фильма
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {

    // Поле для хранения идентификатора рейтинга
    private int id;

    // Поле для хранения названия рейтинга
    private String name;
}
