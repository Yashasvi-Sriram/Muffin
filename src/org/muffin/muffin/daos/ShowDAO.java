package org.muffin.muffin.daos;

import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Show;
import org.muffin.muffin.beans.Showtime;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShowDAO {
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime);

    public Optional<Show> get(int movieId, int theatreId, Showtime showtime);

    public List<Show> get(int theatreId);

    public Optional<Show> getShow(int showId);

    public boolean delete(int showId, int cinemaBuildingOwnerId);

    public List<Movie> getActiveMovies(String pattern, int limit, int offset, LocalDateTime currentTimeStam);

    public Map<CinemaBuilding, List<Show>> getAllShows(int movieId, String city, String state, String country, Showtime showtime);
}
