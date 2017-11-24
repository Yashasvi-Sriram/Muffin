SELECT *
FROM booked_show_seats;

SELECT *
FROM booking;

SELECT
  s.id,
  s.theatre_id,
  s.x,
  s.y
FROM booked_show_seats AS bss, seat AS s
WHERE bss.seat_id = s.id AND bss.show_id = 2;