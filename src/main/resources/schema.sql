DROP TABLE IF EXISTS film_genres, likes, friends, film_directors, directors, films, genres, users, mpa, feed, reviews, reviews_likes;

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  mpa_name VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(200),
  release_date DATE NOT NULL,
  duration INTEGER NOT NULL,
  mpa_id INTEGER REFERENCES mpa (mpa_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  genre_name VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE NOT NULL,
  genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT unique_pair UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
  user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  login VARCHAR(300) NOT NULL,
  name VARCHAR(255),
  birthday DATE NOT NULL,
  CONSTRAINT uc_users_email UNIQUE (email),
  CONSTRAINT uc_users_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
  friend_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE NOT NULL,
  user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT likes_pk PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS directors (
  director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors (
  film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE NOT NULL,
  director_id INTEGER REFERENCES directors (director_id) ON DELETE CASCADE NOT NULL,
  CONSTRAINT unique_film_director UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES films(film_id) ON DELETE CASCADE,
    useful INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    review_id INTEGER REFERENCES reviews(review_id) ON DELETE CASCADE NOT NULL,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE NOT NULL,
    is_useful BOOLEAN NOT NULL,
    CONSTRAINT reviews_likes_pk PRIMARY KEY (review_id, user_id)
);

CREATE TABLE if not EXISTS feed (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    timestamp BIGINT NOT NULL,
    event_type VARCHAR NOT NULL,
    operation VARCHAR NOT NULL,
    entity_id INTEGER NOT NULL
);