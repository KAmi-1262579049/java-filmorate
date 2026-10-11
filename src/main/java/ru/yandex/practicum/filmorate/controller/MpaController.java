package ru.yandex.practicum.filmorate.controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;
import java.util.List;
@RestController @RequestMapping("/mpa")
// Класс MpaController для обработки HTTP-запросов, связанных с рейтингами MPA
public class MpaController {
    // Поле для хранения объекта, который работает с рейтингами в базе данных
    private final MpaDbStorage storage;
    // Конструктор класса
    public MpaController(MpaDbStorage storage){
        this.storage=storage;
    }

    @GetMapping
    // Метод для получения списка всех рейтингов MPA
    public List<Mpa> findAll(){
        return storage.findAll();
    }

    @GetMapping("/{id}")
    // Метод для получения рейтинга MPA по его идентификатору
    public Mpa getById(@PathVariable int id){
        return storage.getById(id).orElseThrow(()->new NotFoundException("Рейтинг не найден: "+id));
    }
}
