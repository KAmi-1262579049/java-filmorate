package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
// Класс InMemoryUserStorage хранит пользователей в памяти приложения
public class InMemoryUserStorage implements UserStorage {

    // Коллекция пользователей, где ключ - id пользователя
    private final Map<Integer, User> users = new HashMap<>();

    // Счётчик для генерации уникальных id
    private int currentId = 0;

    @Override
    // Создаёт нового пользователя и сохраняет его в памяти
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    // Обновляет пользователя в хранилище
    public User update(User user) {
        User oldUser = users.get(user.getId());
        user.getFriends().addAll(oldUser.getFriends());
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    // Удаляет пользователя по id
    public void delete(int id) {
        users.remove(id);
        log.info("Удалён пользователь с id={}", id);
    }

    @Override
    // Возвращает пользователя по id
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    // Возвращает список всех пользователей
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    // Генерирует следующий уникальный id
    private int getNextId() {
        return ++currentId;
    }
}
