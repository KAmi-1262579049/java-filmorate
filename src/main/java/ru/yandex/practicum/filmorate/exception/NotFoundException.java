package ru.yandex.practicum.filmorate.exception;

// Класс для обработки ситуаций, когда пользователь или фильм не найдены
public class NotFoundException extends RuntimeException {

    // Конструктор исключения
    public NotFoundException(String message) {
        super(message);
    }
}
