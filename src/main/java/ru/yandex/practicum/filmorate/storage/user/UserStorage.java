package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

// Интерфейс UserStorage описывает операции хранения пользователей
public interface UserStorage {

    // Добавляет нового пользователя в хранилище
    User create(User user);

    // Обновляет уже существующего пользователя
    User update(User user);

    // Удаляет пользователя по id
    void delete(int id);

    // Возвращает пользователя по id
    User getById(int id);

    // Возвращает всех пользователей
    Collection<User> findAll();
}
