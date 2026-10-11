package ru.yandex.practicum.filmorate.controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import java.util.List;
@RestController
@RequestMapping("/genres")
// Класс для обработки HTTP-запросов, связанных с жанрами
public class GenreController {
    // Поле для хранения объекта, который работает с жанрами в базе данных
    private final GenreDbStorage storage;
    // Конструктор класса
    public GenreController(GenreDbStorage storage) {
        this.storage=storage;
    }

    @GetMapping
    // Метод для получения списка всех жанров из базы данных
    public List<Genre> findAll(){
        return storage.findAll();
    }
    
    @GetMapping("/{id}")
    // Метод для получения жанра по его идентификатору
    public Genre getById(@PathVariable int id){
        return storage.getById(id).orElseThrow(()->new NotFoundException("Жанр не найден: "+id));
    }
}
