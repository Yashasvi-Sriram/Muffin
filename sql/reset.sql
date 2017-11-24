DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS muff_likes_actor;
DROP TABLE IF EXISTS movie_genre_r;
-- DROP TABLE IF EXISTS muff_likes_character;
DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS booked_show_seats;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS show;
DROP EXTENSION IF EXISTS btree_gist;
DROP TABLE IF EXISTS follows;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS theatre;
DROP TABLE IF EXISTS cinema_building;
DROP TABLE IF EXISTS cinema_building_owner_password;
DROP TABLE IF EXISTS cinema_building_owner;
DROP TABLE IF EXISTS seek_response;
DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS movie_owner_password;
DROP TABLE IF EXISTS movie_owner;
DROP TABLE IF EXISTS seek_genre_r;
DROP TABLE IF EXISTS seek;
DROP TABLE IF EXISTS genre;
-- DROP TABLE IF EXISTS comment_on_post;
-- DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS muff_password;
DROP TABLE IF EXISTS muff;
DROP TABLE IF EXISTS valid_region;

/*
  Anything can be done with data as long as the sql constraints are followed
  The idea is to put max possible constraints in the sql space itself
  and give responsibility of the rest to the high level language above db
*/

/*
  Any not null unique field can be moved into primary key
  But better to move it only if the fields do not tend to update in tuple's lifecycle
  as updating fks of dependents is really messy
*/

/*
  In general if primary key is unique and constant for a tuple through out its lifetime
  (from creation to deletion) then operations are simplified. So if there is a need to update any
  field which could be a primary key, one could create a (synthetic if you may) primary key field
  which can be unique and constant through out the life time of the tuple
*/

/*            */
/* Muff stuff */
/*            */
-- if handle is pk handle should not be allowed to update as it needs complex update chains
-- & takes too long db operation
-- of course a muff should be allowed to delete
-- in which case all the dependents are cascaded
-- BUT,
-- If we use another field id and use that as primary key
-- then the handle field does not have any dependents in terms of fks
-- then handle being unique is enough
-- now it can be easily changed without complex update chains or much db time
-- handle update can be provided
CREATE TABLE muff (
  id           SERIAL,
  handle       VARCHAR(50) NOT NULL CHECK (handle <> ''),
  name         VARCHAR(50) NOT NULL CHECK (name <> ''),
  no_approvals INT         NOT NULL DEFAULT 0,
  joined_on    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (handle)
);

-- password update can be provided
CREATE TABLE muff_password (
  id       INT,
  password VARCHAR(50) NOT NULL CHECK (password <> ''),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES muff (id)
  ON DELETE CASCADE
);

-- follow and un-follow operations can be supported
CREATE TABLE follows (
  id1 INT,
  id2 INT,
  PRIMARY KEY (id1, id2),
  FOREIGN KEY (id1) REFERENCES muff (id)
  ON DELETE CASCADE,
  FOREIGN KEY (id2) REFERENCES muff (id)
  ON DELETE CASCADE
);

/*                   */
/* Movie Owner stuff */
/*                   */
-- handle update can be provided
CREATE TABLE movie_owner (
  id        SERIAL,
  handle    VARCHAR(50) NOT NULL CHECK (handle <> ''),
  name      VARCHAR(50) NOT NULL CHECK (name <> ''),
  joined_on TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (handle)
);

-- password update can be provided
CREATE TABLE movie_owner_password (
  id       INT,
  password VARCHAR(50) NOT NULL CHECK (password <> ''),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES movie_owner (id)
  ON DELETE CASCADE
);

-- name update can be allowed
CREATE TABLE movie (
  id       SERIAL,
  name     VARCHAR(100) NOT NULL CHECK (name <> ''),
  owner_id INT          NOT NULL,
  duration INT          NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name),
  UNIQUE (id, owner_id),
  FOREIGN KEY (owner_id) REFERENCES movie_owner (id)
  ON DELETE CASCADE
);

-- name update can be allowed
-- as of now actors don't have an account
CREATE TABLE actor (
  id   SERIAL,
  name VARCHAR(50) NOT NULL CHECK (name <> ''),
  PRIMARY KEY (id),
  UNIQUE (name)
);

-- name update can be allowed
CREATE TABLE character (
  id             SERIAL,
  name           VARCHAR(50) NOT NULL CHECK (name <> ''),
  movie_id       INT         NOT NULL,
  movie_owner_id INT         NOT NULL,
  actor_id       INT         NOT NULL,
  PRIMARY KEY (id),
  --   name is unique per movie basis
  UNIQUE (movie_id, name),
  FOREIGN KEY (movie_id, movie_owner_id) REFERENCES movie (id, owner_id)
  ON DELETE CASCADE,
  FOREIGN KEY (actor_id) REFERENCES actor (id)
  ON DELETE CASCADE
);

/*                               */
/* Cinema (Building) Owner Stuff */
/*                               */
-- handle update can be provided
CREATE TABLE cinema_building_owner (
  id        SERIAL,
  handle    VARCHAR(50) NOT NULL CHECK (handle <> ''),
  name      VARCHAR(50) NOT NULL CHECK (name <> ''),
  joined_on TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (handle),
  PRIMARY KEY (id)
);

-- password update can be provided
CREATE TABLE cinema_building_owner_password (
  id       INT,
  password VARCHAR(50) NOT NULL CHECK (password <> ''),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES cinema_building_owner (id)
  ON DELETE CASCADE
);

CREATE TABLE valid_region (
  id      SERIAL,
  city    VARCHAR(50) NOT NULL CHECK (city <> ''),
  state   VARCHAR(50) NOT NULL CHECK (state <> ''),
  country VARCHAR(50) NOT NULL CHECK (country <> ''),
  PRIMARY KEY (id),
  UNIQUE (city, state, country)
);

CREATE TABLE cinema_building (
  id          SERIAL,
  owner_id    INT         NOT NULL,
  name        VARCHAR(50) NOT NULL CHECK (name <> ''),
  --   address fields, format closest to google maps api
  street_name VARCHAR(50) NOT NULL CHECK (street_name <> ''),
  city        VARCHAR(50) NOT NULL CHECK (city <> ''),
  state       VARCHAR(50) NOT NULL CHECK (state <> ''),
  country     VARCHAR(50) NOT NULL CHECK (country <> ''),
  zip         VARCHAR(50) NOT NULL CHECK (zip <> ''),
  PRIMARY KEY (id),
  --   though even if these fields are unique they may point to same cinema building
  --   this is the least we can do in db space
  UNIQUE (name, street_name, city, state, zip, country),
  FOREIGN KEY (city, state, country) REFERENCES valid_region (city, state, country)
  ON DELETE CASCADE,
  FOREIGN KEY (owner_id) REFERENCES cinema_building_owner (id)
  ON DELETE CASCADE
);

CREATE TABLE theatre (
  id                 SERIAL,
  cinema_building_id INT NOT NULL,
  screen_no          INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (cinema_building_id, screen_no),
  FOREIGN KEY (cinema_building_id) REFERENCES cinema_building (id)
  ON DELETE CASCADE
);

CREATE TABLE seat (
  id         SERIAL,
  theatre_id INT NOT NULL,
  -- x = column, y = row
  x          INT NOT NULL,
  y          INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (theatre_id, x, y),
  UNIQUE (id, theatre_id),
  FOREIGN KEY (theatre_id) REFERENCES theatre (id)
  ON DELETE CASCADE
);

CREATE EXTENSION btree_gist;

CREATE TABLE show (
  id         SERIAL,
  theatre_id INT NOT NULL,
  movie_id   INT NOT NULL,
  -- if the format of datetime is made YYYY MM DD HH MinMin SS
  -- then comparision can be done directly as strings
  during     TSRANGE,
  PRIMARY KEY (id),
  UNIQUE (id, theatre_id),
  FOREIGN KEY (theatre_id) REFERENCES theatre (id)
  ON DELETE CASCADE,
  FOREIGN KEY (movie_id) REFERENCES movie (id)
  ON DELETE CASCADE,
  EXCLUDE USING GIST (theatre_id WITH =, during WITH &&)
);

/* Muff stuff again */
CREATE TABLE muff_likes_actor (
  muff_id  INT,
  actor_id INT,
  PRIMARY KEY (muff_id, actor_id),
  FOREIGN KEY (muff_id) REFERENCES muff (id)
  ON DELETE CASCADE,
  FOREIGN KEY (actor_id) REFERENCES actor (id)
  ON DELETE CASCADE
);

-- CREATE TABLE muff_likes_character (
--   muff_id      INT,
--   character_id INT,
--   PRIMARY KEY (muff_id, character_id),
--   FOREIGN KEY (muff_id) REFERENCES muff (id)
--   ON DELETE CASCADE,
--   FOREIGN KEY (character_id) REFERENCES character (id)
--   ON DELETE CASCADE
-- );

CREATE TABLE review (
  id        SERIAL,
  muff_id   INT           NOT NULL,
  movie_id  INT           NOT NULL,
  rating    NUMERIC(3, 1) NOT NULL CHECK (rating >= 0.0 AND rating <= 10.0), -- Ex: 07.4 / 10.0
  text      TEXT          NOT NULL,
  timestamp TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (muff_id, movie_id),
  FOREIGN KEY (muff_id) REFERENCES muff (id)
  ON DELETE CASCADE,
  FOREIGN KEY (movie_id) REFERENCES movie (id)
  ON DELETE CASCADE
);

CREATE TABLE seek (
  id        SERIAL,
  muff_id   INT       NOT NULL,
  text      TEXT      NOT NULL,
  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (muff_id) REFERENCES muff (id)
  ON DELETE CASCADE
);

CREATE TABLE seek_response (
  id              SERIAL,
  muff_id         INT       NOT NULL,
  seek_id         INT       NOT NULL,
  movie_id        INT       NOT NULL,
  text            TEXT      NOT NULL,
  approval_status INT       NOT NULL DEFAULT 0,
  timestamp       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (muff_id, seek_id),
  FOREIGN KEY (muff_id) REFERENCES muff (id)
  ON DELETE CASCADE,
  FOREIGN KEY (seek_id) REFERENCES seek (id)
  ON DELETE CASCADE,
  FOREIGN KEY (movie_id) REFERENCES movie (id)
  ON DELETE CASCADE
);

-- CREATE TABLE post (
--   id        SERIAL,
--   muff_id   INT       NOT NULL,
--   text      TEXT      NOT NULL,
--   timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--   PRIMARY KEY (id),
--   FOREIGN KEY (muff_id) REFERENCES muff (id)
--   ON DELETE CASCADE
-- );
--
-- CREATE TABLE comment_on_post (
--   id        SERIAL,
--   muff_id   INT       NOT NULL,
--   post_id   INT       NOT NULL,
--   text      TEXT      NOT NULL,
--   timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--   PRIMARY KEY (id),
--   FOREIGN KEY (muff_id) REFERENCES muff (id)
--   ON DELETE CASCADE,
--   FOREIGN KEY (post_id) REFERENCES post (id)
--   ON DELETE CASCADE
-- );

CREATE TABLE booking (
  id      SERIAL,
  show_id INT NOT NULL,
  muff_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (id, show_id),
  FOREIGN KEY (show_id) REFERENCES show (id)
  ON DELETE CASCADE,
  FOREIGN KEY (muff_id) REFERENCES muff (id)
  ON DELETE CASCADE
);

CREATE TABLE booked_show_seats (
  id         SERIAL,
  theatre_id INT NOT NULL,
  seat_id    INT NOT NULL,
  show_id    INT NOT NULL,
  booking_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (booking_id, show_id, seat_id),
  FOREIGN KEY (seat_id, theatre_id) REFERENCES seat (id, theatre_id)
  ON DELETE CASCADE,
  FOREIGN KEY (show_id, theatre_id) REFERENCES show (id, theatre_id)
  ON DELETE CASCADE,
  FOREIGN KEY (booking_id, show_id) REFERENCES booking (id, show_id)
  ON DELETE CASCADE
);

CREATE TABLE genre (
  id   SERIAL,
  name VARCHAR(50) NOT NULL CHECK (name <> ''),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE movie_genre_r (
  movie_id INT,
  genre_id INT,
  PRIMARY KEY (movie_id, genre_id),
  FOREIGN KEY (movie_id) REFERENCES movie (id)
  ON DELETE CASCADE,
  FOREIGN KEY (genre_id) REFERENCES genre (id)
  ON DELETE CASCADE
);

CREATE TABLE seek_genre_r (
  seek_id  INT NOT NULL,
  genre_id INT NOT NULL,
  PRIMARY KEY (seek_id, genre_id),
  FOREIGN KEY (seek_id) REFERENCES seek (id)
  ON DELETE CASCADE,
  FOREIGN KEY (genre_id) REFERENCES genre (id)
  ON DELETE CASCADE
);