INSERT INTO MPA (mpa_id, mpa_name) values (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

INSERT INTO GENRES (genre_id, genre_name) values (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'),
(5, 'Документальный'), (6, 'Боевик');

INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('Экстремальная работа', 'У детективов не получается ловить подозреваемых, зато прекрасно выходит жареная курочка', '2019-10-01', '6660', '3');
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('Шоу Трумана', 'Будет ли Труман продолжать жить в безопасном мире, где, как он теперь знает, у него практически нет свободы выбора', '1998-11-21', '6180', '2');
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('Ходячий замок', 'Девушка-бабушка бежит из города куда глаза глядят и встречает удивительный дом на ножках', '2004-08-14', '7140', '1');
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES ('Чебурашка', 'Одержимое апельсинами животное оказывается в домике нелюдимого старика-садовника Геннадия', '2022-01-18', '6780', '1');

INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('1', '1');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('1', '2');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('1', '6');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('2', '1');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('2', '2');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('3', '3');
INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES ('4', '1');

INSERT INTO USERS (name, email, login, birthday) VALUES ('Василий', 'vasya@mail.ru', 'vasya33', '2000-01-18');
INSERT INTO USERS (name, email, login, birthday) VALUES ('Дмитрий', 'dima@mail.ru', 'dima55', '1992-11-08');
INSERT INTO USERS (name, email, login, birthday) VALUES ('Аня', 'anya@mail.ru', 'anya77', '2004-05-05');
INSERT INTO USERS (name, email, login, birthday) VALUES ('Владимир', 'vova@mail.ru', 'vova11', '1993-03-15');
INSERT INTO USERS (name, email, login, birthday) VALUES ('Дарья', 'dasha@mail.ru', 'dasha18', '2001-04-11');

INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('1', '1');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('1', '2');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('1', '3');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('1', '4');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('2', '3');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('3', '2');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('3', '3');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('3', '4');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('4', '3');
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ('4', '5');