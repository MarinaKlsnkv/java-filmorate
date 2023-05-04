# java-filmorate
Template repository for Filmorate project.

# Database schema

![db_schema](src/main/resources/img/filmorate_db_schema.png)

# Database script

```postgres-psql
CREATE TABLE films (
  id serial PRIMARY KEY,
  name varchar(255) not null,
  description varchar(255),
  release_date date,
  duration smallint,  
  mpaRating varchar(10)
);

CREATE TABLE genres (
  id serial PRIMARY KEY,
  name varchar(255) not null
);

CREATE TABLE film_genre (
  id serial PRIMARY KEY,
  film_id bigint not null,
  genre_id bigint not null,
  FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
  FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE users (
  id serial PRIMARY KEY,
  email varchar(100) UNIQUE NOT NULL,
  login varchar(50) UNIQUE NOT NULL,
  name varchar(100),
  birthday date
);

CREATE TABLE friendship (
  id serial PRIMARY KEY,
  user_id bigint REFERENCES users(id) ON DELETE CASCADE,
  friend_id bigint REFERENCES users(id) ON DELETE CASCADE,
  confirmed boolean not null default false
);

CREATE TABLE film_likes (
  id serial PRIMARY KEY,
  user_id bigint not null,
  film_id bigint not null,  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE
);

```
