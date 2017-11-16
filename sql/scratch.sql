SELECT
  seek.id,
  muff.id, muff.handle, muff.name, muff.level, muff.joined_on,
  seek.text,
  seek.timestamp
FROM muff, seek
WHERE muff.id = ? AND muff.id = seek.muff_id AND seek.timestamp <= ?
ORDER BY seek.timestamp DESC
OFFSET ?
LIMIT ?;


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
