package org.muffin.muffin.daos;


import javafx.util.Pair;
import lombok.NonNull;
import org.muffin.muffin.beans.Seat;
import org.muffin.muffin.beans.Theatre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TheatreDAO {
    public List<Theatre> getByCinemaBuilding(final int cinemaBuildingId, final int cinemaBuildingOwnerId);

    public Optional<Theatre> create(final int cinemaBuildingId, final int screenNo, final int cinemaBuildingOwnerId);

    public Optional<Theatre> get(final int cinemaBuildingId, final int screenNo);

    public Optional<Theatre> getByOwner(final int theatreId, final int cinemaBuildingOwnerId);

    public boolean delete(final int theatreId, final int cinemaBuildingOwnerId);

    public List<Seat> getSeatsOf(final int theatreID);

    public boolean createSeatsOfTheatre(final int theatreID, @NonNull final Set<Pair<Integer, Integer>> seatsXY);

    public Optional<Theatre> getById(final int theatreId);
}
