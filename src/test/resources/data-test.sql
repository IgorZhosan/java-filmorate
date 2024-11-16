INSERT INTO mpa (mpa_name)
VALUES ('G');
INSERT INTO mpa (mpa_name)
VALUES ('PG');
INSERT INTO mpa (mpa_name)
VALUES ('PG-13');
INSERT INTO mpa (mpa_name)
VALUES ('R');
INSERT INTO mpa (mpa_name)
VALUES ('NC-17');

INSERT INTO genres (genre_name)
VALUES ('Комедия');

INSERT INTO genres (genre_name)
VALUES ('Драма');

INSERT INTO genres (genre_name)
VALUES ('Мультфильм');

INSERT INTO genres (genre_name)
VALUES ('Триллер');

INSERT INTO genres (genre_name)
VALUES ('Документальный');

INSERT INTO genres (genre_name)
VALUES ('Боевик');

/* Тестовые данные */

INSERT INTO films (film_name, mpa_id, description, release_date, duration)
VALUES ('Терминатор', 1, 'Большой железный', '1990-05-19', 90);

INSERT INTO films (film_name, mpa_id, description, release_date, duration)
VALUES ('Аватар', 2, 'Синие человечки', '2017-04-01', 150);

INSERT INTO films (film_name, mpa_id, description, release_date, duration)
VALUES ('Побег из Шоушенка', 4, 'Побег', '1985-05-01', 70);


INSERT INTO users (email, login, user_name, birthday)
VALUES ('first@yandex.ru', 'Petro', 'Петр', '1990-05-19');

INSERT INTO users (email, login, user_name, birthday)
VALUES ('second@yandex.ru', 'Bigi', 'Лёха', '1985-02-20');

INSERT INTO users (email, login, user_name, birthday)
VALUES ('third@yandex.ru', 'Gena', 'Гена', '1996-01-05');


INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 1);

INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 2);

INSERT INTO films_genres (film_id, genre_id)
VALUES (2, 3);

INSERT INTO films_genres (film_id, genre_id)
VALUES (3, 1);

INSERT INTO films_genres (film_id, genre_id)
VALUES (3, 2);

INSERT INTO films_genres (film_id, genre_id)
VALUES (3, 3);

INSERT INTO users_friends (user_id, friend_id)
VALUES (1, 2);

INSERT INTO users_friends (user_id, friend_id)
VALUES (1, 3);

INSERT INTO users_friends (user_id, friend_id)
VALUES (2, 1);

INSERT INTO users_friends (user_id, friend_id)
VALUES (2, 3);


INSERT INTO users_films_likes (user_id, film_id)
VALUES (1, 1);

INSERT INTO users_films_likes (user_id, film_id)
VALUES (1, 2);

INSERT INTO users_films_likes (user_id, film_id)
VALUES (1, 3);

INSERT INTO users_films_likes (user_id, film_id)
VALUES (2, 1);

INSERT INTO users_films_likes (user_id, film_id)
VALUES (2, 2);

INSERT INTO users_films_likes (user_id, film_id)
VALUES (3, 1);