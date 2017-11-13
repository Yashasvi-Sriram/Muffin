package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Show;
import org.muffin.muffin.beans.Showtime;

import java.util.Optional;

public interface ShowDAO {
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime);

    public Optional<Show> get(int movieId, int theatreId, Showtime showtime);

    public boolean delete(int showId, int cinemaBuildingOwnerId);
}
