package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
// Класс UserController обрабатывает HTTP-запросы, связанные с пользователями
public class UserController {

    // Сервис пользователей, содержащий бизнес-логику
    private final UserService userService;

    @Autowired
    // Конструктор получает UserService из Spring-контекста
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Обрабатывает POST-запрос на создание пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    // Обрабатывает PUT-запрос на обновление пользователя
    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    // Обрабатывает GET-запрос на получение всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    // Обрабатывает GET-запрос на получение пользователя по id
    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // Обрабатывает PUT-запрос на добавление пользователя в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    // Обрабатывает DELETE-запрос на удаление пользователя из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    // Обрабатывает GET-запрос на получение друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    // Обрабатывает GET-запрос на получение общих друзей двух пользователей
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
