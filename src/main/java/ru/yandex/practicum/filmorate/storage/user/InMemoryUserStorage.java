package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        validate(user);
        fillName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    // Обновляет пользователя, если пользователь с таким id существует
    public User update(User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        fillName(user);
        User oldUser = users.get(user.getId());
        user.getFriends().addAll(oldUser.getFriends());
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    // Удаляет пользователя по id
    public void delete(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        users.remove(id);
        log.info("Удалён пользователь с id={}", id);
    }

    @Override
    // Возвращает пользователя по id
    public User getById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
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
