package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
// Класс User хранит данные пользователя приложения
public class User {

    // Уникальный идентификатор пользователя
    private int id;
    // Электронная почта пользователя
    private String email;
    // Логин пользователя
    private String login;
    // Имя пользователя для отображения
    private String name;
    // Дата рождения пользователя
    private LocalDate birthday;
    // Набор id друзей пользователя
    @JsonIgnore
    private final Set<Integer> friends = new HashSet<>();
}
