
package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Класс для интеграционного тестирования работы с базой данных
@JdbcTest
@Import({UserDbStorage.class, FilmDbStorage.class})
class DbStorageIntegrationTest {

    // Поле для хранения экземпляра хранилища пользователей
    @Autowired
    UserDbStorage users;

    // Поле для хранения экземпляра хранилища фильмов
    @Autowired
    FilmDbStorage films;

    // Тест для проверки создания, получения, обновления, удаления пользователей и работы с дружбой
    @Test
    void userCrudAndFriends() {
        // Создаём первого пользователя
        User a = new User();
        a.setEmail("a@a.ru");
        a.setLogin("a");
        a.setName("A");
        a.setBirthday(LocalDate.of(2000, 1, 1));

        // Создаём второго пользователя
        User b = new User();
        b.setEmail("b@b.ru");
        b.setLogin("b");
        b.setName("B");
        b.setBirthday(LocalDate.of(2000, 1, 1));

        // Сохраняем пользователей в базе данных
        users.create(a);
        users.create(b);

        // Проверяем, что первый пользователь существует
        assertThat(users.getById(a.getId())).isPresent();

        // Добавляем второго пользователя в друзья первому
        users.addFriend(a.getId(), b.getId());

        // Проверяем одностороннюю дружбу
        assertThat(users.getFriends(a.getId())).hasSize(1);
        assertThat(users.getFriends(b.getId())).isEmpty();

        // Удаляем второго пользователя из друзей первого
        users.removeFriend(a.getId(), b.getId());

        // Проверяем, что список друзей стал пустым
        assertThat(users.getFriends(a.getId())).isEmpty();

        // Изменяем имя первого пользователя
        a.setName("Changed");

        // Обновляем данные пользователя
        users.update(a);

        // Проверяем сохранение нового имени
        assertThat(users.getById(a.getId()).orElseThrow().getName())
                .isEqualTo("Changed");

        // Проверяем количество пользователей в базе данных
        assertThat(users.findAll()).hasSize(2);

        // Удаляем первого пользователя
        users.delete(a.getId());

        // Проверяем, что пользователь удалён
        assertThat(users.getById(a.getId())).isEmpty();
    }

    // Тест для проверки создания, получения, обновления, удаления фильмов и работы с лайками
    @Test
    void filmCrudAndLikes() {
        // Создаём пользователя для проверки лайков
        User user = new User();
        user.setEmail("x@x.ru");
        user.setLogin("x");
        user.setName("X");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Сохраняем пользователя в базе данных
        users.create(user);

        // Создаём новый фильм
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(90);

        // Сохраняем фильм в базе данных
        films.create(film);

        // Проверяем, что фильм существует
        assertThat(films.getById(film.getId())).isPresent();

        // Добавляем лайк фильму от пользователя
        films.addLike(film.getId(), user.getId());

        // Проверяем список популярных фильмов
        assertThat(films.getPopularFilms(10)).hasSize(1);

        // Проверяем, что лайк пользователя сохранён
        assertThat(films.getById(film.getId()).orElseThrow().getLikes())
                .contains(user.getId());

        // Удаляем лайк пользователя
        films.removeLike(film.getId(), user.getId());

        // Изменяем название фильма
        film.setName("Updated");

        // Обновляем фильм в базе данных
        films.update(film);

        // Проверяем количество фильмов в базе данных
        assertThat(films.findAll()).hasSize(1);

        // Проверяем, что название фильма обновилось
        assertThat(films.getById(film.getId()).orElseThrow().getName())
                .isEqualTo("Updated");

        // Удаляем фильм из базы данных
        films.delete(film.getId());

        // Проверяем, что фильм больше не существует
        assertThat(films.getById(film.getId())).isEmpty();
    }
}
