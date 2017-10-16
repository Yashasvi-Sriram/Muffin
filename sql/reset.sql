DROP SEQUENCE IF EXISTS movie_actor_character_seq;
DROP TABLE IF EXISTS movie_actor_character_r;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS follows;
DROP TABLE IF EXISTS password;
DROP TABLE IF EXISTS muff;

/* Anything can be done with data as long as the sql constraints are followed
   The idea is to put max possible constraints in the sql space itself
   and give responsibility of the rest to the high level language */

/*            */
/* Muff stuff */
/*            */
-- handle should not be allowed to update
-- needs complex update chains
-- takes too long db operation
-- of course a row should be allowed to delete
-- in which case all the dependents are cascaded
CREATE TABLE muff (
  handle VARCHAR(50),
  name   VARCHAR(50) NOT NULL,
  PRIMARY KEY (handle)
);

-- password can be changed
CREATE TABLE password (
  handle   VARCHAR(50),
  password VARCHAR(50) NOT NULL,
  PRIMARY KEY (handle),
  FOREIGN KEY (handle) REFERENCES muff (handle)
  ON DELETE CASCADE
);

-- follow and un-follow operations should be supported
CREATE TABLE follows (
  handle1 VARCHAR(50),
  handle2 VARCHAR(50),
  PRIMARY KEY (handle1, handle2),
  FOREIGN KEY (handle1) REFERENCES muff (handle)
  ON DELETE CASCADE,
  FOREIGN KEY (handle2) REFERENCES muff (handle)
  ON DELETE CASCADE
);

/*             */
/* Movie stuff */
/*             */
-- name should be allowed to change
CREATE TABLE movie (
  name VARCHAR(50),
  PRIMARY KEY (name)
);

-- name should be allowed to change
CREATE TABLE actor (
  name VARCHAR(50),
  PRIMARY KEY (name)
);

-- name should be allowed to change
CREATE TABLE character (
  name VARCHAR(50),
  PRIMARY KEY (name)
);

-- many to many
CREATE TABLE movie_actor_character_r (
  id             INT PRIMARY KEY,
  movie_name     VARCHAR(50) NOT NULL,
  actor_name     VARCHAR(50) NOT NULL,
  character_name VARCHAR(50) NOT NULL,
  FOREIGN KEY (movie_name) REFERENCES movie (name)
  ON DELETE CASCADE,
  FOREIGN KEY (actor_name) REFERENCES actor (name)
  ON DELETE CASCADE,
  FOREIGN KEY (character_name) REFERENCES character (name)
  ON DELETE CASCADE,
  UNIQUE (movie_name, actor_name, character_name)
);

CREATE SEQUENCE movie_actor_character_seq;

INSERT INTO movie (name) VALUES ('The Croods');
INSERT INTO movie (name) VALUES ('Rear Window');
INSERT INTO movie (name) VALUES ('Indiana Jones');
INSERT INTO movie (name) VALUES ('Imitation Game');
INSERT INTO movie (name) VALUES ('Birdman');

INSERT INTO actor (name) VALUES ('Emma Stone');
INSERT INTO actor (name) VALUES ('Ryan Reynolds');
INSERT INTO actor (name) VALUES ('Bennedict Cumberbatch');
INSERT INTO actor (name) VALUES ('Micheal Keaton');

INSERT INTO character (name) VALUES ('Jeff');
INSERT INTO character (name) VALUES ('Dr. Jones');
INSERT INTO character (name) VALUES ('Alan Turing');
INSERT INTO character (name) VALUES ('The guy');
INSERT INTO character (name) VALUES ('Belt');

INSERT INTO movie_actor_character_r (id, movie_name, actor_name, character_name)
VALUES (nextval('movie_actor_character_seq'), 'The Croods', 'Ryan Reynolds', 'The guy');
