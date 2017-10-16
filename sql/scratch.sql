-- setup
INSERT INTO muff (handle, name) VALUES ('a', 'fasjdfadsf');
INSERT INTO muff (handle, name) VALUES ('b', 'fasjdfadsf');

INSERT INTO follows (handle1, handle2) VALUES ('a', 'b');

DELETE FROM follows WHERE handle1 = 'a';

-- manipulate
UPDATE muff
SET handle = 'd'
WHERE handle = 'b';

DELETE FROM follows
WHERE handle1 = 'c';

UPDATE follows
SET handle1 = 'd', handle2 = 'd'
WHERE handle1 = 'b';

SELECT *
FROM follows;

SELECT *
FROM muff;
