INSERT INTO muff (handle, name) VALUES ('madsc', 'yashasvi');
INSERT INTO muff (handle, name) VALUES ('theone', 'sam');
INSERT INTO muff (handle, name) VALUES ('hucklecliff', 'robin');
INSERT INTO muff (handle, name) VALUES ('@Iamben', 'ben');

INSERT INTO muff_password (id, password) VALUES (1, '.');
INSERT INTO muff_password (id, password) VALUES (2, '.');
INSERT INTO muff_password (id, password) VALUES (3, '.');
INSERT INTO muff_password (id, password) VALUES (4, '.');

INSERT INTO follows (id1, id2) VALUES (1, 2);
INSERT INTO follows (id1, id2) VALUES (2, 3);
INSERT INTO follows (id1, id2) VALUES (3, 1);
INSERT INTO follows (id1, id2) VALUES (4, 1);
INSERT INTO follows (id1, id2) VALUES (4, 4);

INSERT INTO movie (name) VALUES ('The Croods');
INSERT INTO movie (name) VALUES ('Rear Window');
INSERT INTO movie (name) VALUES ('Indiana Jones');
INSERT INTO movie (name) VALUES ('Imitation Game');
INSERT INTO movie (name) VALUES ('Birdman');

INSERT INTO actor (name) VALUES ('Emma Stone');
INSERT INTO actor (name) VALUES ('Ryan Reynolds');
INSERT INTO actor (name) VALUES ('Bennedict Cumberbatch');
INSERT INTO actor (name) VALUES ('Micheal Keaton');
INSERT INTO actor (name) VALUES ('Stewart');

INSERT INTO character (name) VALUES ('Jeff');
INSERT INTO character (name) VALUES ('Dr. Jones');
INSERT INTO character (name) VALUES ('Alan Turing');
INSERT INTO character (name) VALUES ('The guy');
INSERT INTO character (name) VALUES ('Belt');

INSERT INTO movie_actor_character_r (movie_id, actor_id, character_id) VALUES (1, 2, 4);
INSERT INTO movie_actor_character_r (movie_id, actor_id, character_id) VALUES (2, 5, 1);
INSERT INTO movie_actor_character_r (movie_id, actor_id, character_id) VALUES (3, 3, 3);

INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (1, 1);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (1, 2);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (1, 3);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (1, 4);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (2, 4);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (2, 1);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (2, 3);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (3, 1);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (3, 4);
INSERT INTO muff_likes_actor (muff_id, actor_id) VALUES (4, 2);


INSERT INTO muff_likes_character (muff_id, character_id) VALUES (1, 1);
INSERT INTO muff_likes_character (muff_id, character_id) VALUES (1, 3);
INSERT INTO muff_likes_character (muff_id, character_id) VALUES (1, 4);
INSERT INTO muff_likes_character (muff_id, character_id) VALUES (1, 5);
INSERT INTO muff_likes_character (muff_id, character_id) VALUES (2, 1);
INSERT INTO muff_likes_character (muff_id, character_id) VALUES (3, 3);