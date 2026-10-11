package ru.yandex.practicum.filmorate.storage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
@JdbcTest
@Import({UserDbStorage.class,FilmDbStorage.class})
// Класс для интеграционного тестирования работы с базой данных
class DbStorageIntegrationTest {
    @Autowired
    // Поле для хранения экземпляра хранилища пользователей
    UserDbStorage users;

    @Autowired
    // Поле для хранения экземпляра хранилища фильмов
    FilmDbStorage films;

    @Test
    // Тест для проверки создания, получения, обновления, удаления пользователей и работы с дружбой
    void userCrudAndFriends() {
        User a=new User();a.setEmail("a@a.ru");a.setLogin("a");a.setName("A");a.setBirthday(LocalDate.of(2000,1,1));
        User b=new User();b.setEmail("b@b.ru");b.setLogin("b");b.setName("B");b.setBirthday(LocalDate.of(2000,1,1));
        users.create(a);users.create(b);assertThat(users.getById(a.getId())).isPresent();
        users.addFriend(a.getId(),b.getId());assertThat(users.getFriends(a.getId())).hasSize(1);assertThat(users.getFriends(b.getId())).isEmpty();
        users.removeFriend(a.getId(),b.getId());assertThat(users.getFriends(a.getId())).isEmpty();
        a.setName("Changed");users.update(a);assertThat(users.getById(a.getId()).orElseThrow().getName()).isEqualTo("Changed");
        assertThat(users.findAll()).hasSize(2);users.delete(a.getId());assertThat(users.getById(a.getId())).isEmpty();
    }

    @Test
    // Тест для проверки создания, получения, обновления, удаления фильмов и работы с лайками
    void filmCrudAndLikes() {
        User user=new User();user.setEmail("x@x.ru");user.setLogin("x");user.setName("X");user.setBirthday(LocalDate.of(2000,1,1));users.create(user);
        Film film=new Film();film.setName("Test");film.setDescription("Description");film.setReleaseDate(LocalDate.of(2001,1,1));film.setDuration(90);
        films.create(film);assertThat(films.getById(film.getId())).isPresent();films.addLike(film.getId(),user.getId());
        assertThat(films.getPopularFilms(10)).hasSize(1);assertThat(films.getById(film.getId()).orElseThrow().getLikes()).contains(user.getId());
        films.removeLike(film.getId(),user.getId());film.setName("Updated");films.update(film);
        assertThat(films.findAll()).hasSize(1);assertThat(films.getById(film.getId()).orElseThrow().getName()).isEqualTo("Updated");
        films.delete(film.getId());assertThat(films.getById(film.getId())).isEmpty();
    }
}
