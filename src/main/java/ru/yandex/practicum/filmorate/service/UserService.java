package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
// Класс UserService содержит бизнес-логику пользователей и друзей
public class UserService {

    // Хранилище пользователей, внедряемое через интерфейс
    private final UserStorage userStorage;

    @Autowired
    // Конструктор получает зависимость UserStorage из Spring-контекста
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Создаёт пользователя
    public User create(User user) {
        validate(user);
        fillName(user);
        return userStorage.create(user);
    }

    // Обновляет пользователя
    public User update(User user) {
        validate(user);
        getById(user.getId());
        fillName(user);
        return userStorage.update(user);
    }

    // Возвращает пользователя по id
    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    // Возвращает всех пользователей
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // Удаляет пользователя
    public void delete(int id) {
        getById(id);
        userStorage.delete(id);
    }

    // Добавляет пользователей друг другу в друзья
    public void addFriend(int userId, int friendId) {
        checkDifferentUsers(userId, friendId);
        User user = getById(userId);
        getById(friendId);
        ((UserDbStorage) userStorage).addFriend(userId, friendId);
        log.info("Пользователи с id={} и id={} стали друзьями", userId, friendId);
    }

    // Удаляет пользователей из друзей друг у друга
    public void removeFriend(int userId, int friendId) {
        checkDifferentUsers(userId, friendId);
        User user = getById(userId);
        getById(friendId);
        ((UserDbStorage) userStorage).removeFriend(userId, friendId);
        log.info("Пользователи с id={} и id={} больше не друзья", userId, friendId);
    }

    // Возвращает список друзей пользователя
    public List<User> getFriends(int userId) {
        getById(userId);
        return ((UserDbStorage) userStorage).getFriends(userId);
    }

    // Возвращает список общих друзей двух пользователей
    public List<User> getCommonFriends(int userId, int otherId) {
        checkDifferentUsers(userId, otherId);
        getById(userId);
        getById(otherId);
        return ((UserDbStorage) userStorage).getCommonFriends(userId, otherId);
    }

    // Проверяет, что пользователь не пытается выполнить операцию сам с собой
    private void checkDifferentUsers(int userId, int otherId) {
        if (userId == otherId) {
            throw new ValidationException("Операция должна выполняться с разными пользователями");
        }
    }

    // Заполняет имя логином, если имя не указано
    private void fillName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    // Проверяет корректность данных пользователя
    private void validate(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь не передан");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
