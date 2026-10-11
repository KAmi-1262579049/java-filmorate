package ru.yandex.practicum.filmorate.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
// Класс Mpa для хранения информации о возрастном рейтинге фильма
public class Mpa {
    // Поле для хранения уникального идентификатора рейтинга
    private int id;
    // Поле для хранения названия рейтинга
    private String name;
}
