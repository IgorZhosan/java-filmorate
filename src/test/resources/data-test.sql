INSERT INTO mpa(mpa_id, mpa_name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

INSERT INTO genres(genre_id, genre_name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

INSERT INTO users(name, login, email, birthday)
VALUES('Sylik', 'Sylya', 'sylik@mail.ru', '2014-07-07'),
      ('Bocha', 'Bochka', 'bochaledi@mail.ru', '2015-05-05'),
      ('Chycha', 'Chych', 'chycha@mail.ru', '2020-01-07'),
      ('Chosya', 'Chosik', 'chosya@mail.ru', '2020-07-01');

INSERT INTO films(name, description, release_date, duration, mpa_id)
    VALUES ('film1', 'description1', '2011-01-01', '150', 1),
           ('film2', 'description2', '2012-12-12', '160', 2),
           ('film3', 'description3', '2013-03-03', '170', 3),
           ('film4', 'description4', '2014-11-14', '130', 4),
           ('film5', 'description5', '2015-02-15', '125', 5),
           ('film6', 'description6', '2016-06-16', '115', 5),
           ('film7', 'description7', '2017-11-17', '103', 5);

INSERT INTO film_genres(film_id, genre_id) VALUES (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 5), (7, 2);
INSERT INTO friends(user_id, friend_id) VALUES (1, 2), (1, 3), (2, 1), (2, 4), (3, 1), (3, 4), (4, 1);