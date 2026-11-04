package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

// Интерфейс UserStorage описывает операции хранения пользователей
public interface UserStorage {

    // Добавляет нового пользователя в хранилище
    User create(User user);

    // Обновляет уже существующего пользователя
    User update(User user);

    // Удаляет пользователя по id
    void delete(int id);

    // Возвращает пользователя по id, если он есть в хранилище
    Optional<User> getById(int id);

    // Добавляет пользователя в друзья
    void addFriend(int userId, int friendId);

    // Удаляет пользователя из друзей
    void removeFriend(int userId, int friendId);

    // Возвращает друзей пользователя
    List<User> getFriends(int userId);

    // Возвращает общих друзей двух пользователей
    List<User> getCommonFriends(int userId, int otherId);

    // Возвращает всех пользователей
    Collection<User> findAll();
}
