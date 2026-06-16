package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
// Класс Film хранит данные фильма приложения
public class Film {

    // Уникальный идентификатор фильма
    private int id;
    // Название фильма
    private String name;
    // Описание фильма
    private String description;
    // Дата выхода фильма
    private LocalDate releaseDate;
    // Продолжительность фильма в минутах
    private int duration;
    // Набор id пользователей, которые поставили лайк фильму
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();
}
