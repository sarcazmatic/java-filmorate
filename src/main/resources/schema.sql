CREATE TABLE IF NOT EXISTS users
(
    user_id       INTEGER,
    email         varchar NOT NULL,
    login         varchar NOT NULL,
    name          varchar NOT NULL,
    birthday_date date    NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS ratings
(
    rating_id INTEGER,
    rating    varchar NOT NULL,
    CONSTRAINT ratings_pk PRIMARY KEY (rating_id)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INTEGER,
    title        varchar      NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date         NOT NULL,
    duration     varchar      NOT NULL,
    rate         integer,
    rating_id    int references ratings (rating_id),
    CONSTRAINT films_pk PRIMARY KEY (film_id)
);

CREATE TABLE IF NOT EXISTS friendships
(
    friendship_id     INTEGER,
    user_id           int     NOT NULL references users (user_id),
    friend_id         int     NOT NULL references users (user_id),
    friendship_status varchar NOT NULL,
    CONSTRAINT friendships_pk PRIMARY KEY (friendship_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_like_id integer,
    film_id      integer NOT NULL references films (film_id) ON DELETE CASCADE,
    user_id      integer NOT NULL references users (user_id) ON DELETE CASCADE,
    CONSTRAINT film_likes_pk PRIMARY KEY (film_like_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id integer NOT NULL,
    genre    varchar NOT NULL,
    CONSTRAINT genres_pk PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_genre_id integer,
    genre_id integer NOT NULL references genres (genre_id),
    film_id  integer NOT NULL references films (film_id) ON DELETE CASCADE,
    CONSTRAINT film_genre_pk PRIMARY KEY (film_genre_id)
);