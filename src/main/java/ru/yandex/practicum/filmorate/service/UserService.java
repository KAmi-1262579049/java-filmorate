package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Создаёт пользователя
    public User create(User user) {
        return userStorage.create(user);
    }

    // Обновляет пользователя
    public User update(User user) {
        return userStorage.update(user);
    }

    // Возвращает пользователя по id
    public User getById(int id) {
        return userStorage.getById(id);
    }

    // Возвращает всех пользователей
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    // Добавляет пользователей друг другу в друзья
    public void addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи с id={} и id={} стали друзьями", userId, friendId);
    }

    // Удаляет пользователей из друзей друг у друга
    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи с id={} и id={} больше не друзья", userId, friendId);
    }

    // Возвращает список друзей пользователя
    public List<User> getFriends(int userId) {
        User user = userStorage.getById(userId);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .toList();
    }

    // Возвращает список общих друзей двух пользователей
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherId);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getById)
                .toList();
    }
}
