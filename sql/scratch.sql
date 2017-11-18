SELECT *
FROM seek_response;

DELETE FROM seek;

SELECT
  sr.id,
  muff.id,
  muff.handle,
  muff.name,
  muff.level,
  muff.joined_on,
  sr.seek_id,
  movie.id,
  movie.name,
  sr.text,
  sr.timestamp
FROM seek_response AS sr, muff, movie
WHERE sr.seek_id = ? AND sr.muff_id = muff.id AND sr.movie_id = movie.id AND sr.timestamp <= ?
ORDER BY sr.timestamp DESC
OFFSET ?
LIMIT ?;