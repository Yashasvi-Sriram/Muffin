SELECT
  seek.id,
  muff.id,
  muff.handle,
  muff.name,
  muff.level,
  muff.joined_on,
  seek.text,
  seek.timestamp
FROM muff, seek, follows
WHERE follows.id1 = ? AND muff.id = follows.id2 AND muff.id = seek.muff_id AND seek.timestamp <= ?
ORDER BY seek.timestamp DESC
OFFSET ?
LIMIT ?;

SELECT id, muff_id, seek_id, movie_id, text, timestamp FROM seek_response WHERE seek_id = ? AND timestamp > ? ORDER BY timestamp OFFSET ? LIMIT ?;

SELECT *
FROM seek_response;

SELECT
  genre.id,
  genre.name
FROM seek, seek_genre_r, genre
WHERE seek.id = 1 AND seek.id = seek_genre_r.seek_id AND seek_genre_r.genre_id = genre.id
ORDER BY seek.timestamp DESC;

SELECT seek_response.*
FROM seek, seek_response
WHERE seek.id = 1 AND seek.id = seek_response.seek_id
ORDER BY seek_response.timestamp DESC;
