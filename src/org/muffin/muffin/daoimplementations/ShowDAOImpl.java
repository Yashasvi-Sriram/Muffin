package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Show;
import org.muffin.muffin.beans.Showtime;
import org.muffin.muffin.daos.ShowDAO;

import java.util.Optional;

public class ShowDAOImpl implements ShowDAO {
    @Override
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime){
      return false;
    }

    @Override
    public Optional<Show> get(int movieId, int theatreId, Showtime showtime){
        return Optional.empty();
    }

    @Override
    public boolean delete(int showId, int cinemaBuildingOwnerId){
        return false;
    }
}
