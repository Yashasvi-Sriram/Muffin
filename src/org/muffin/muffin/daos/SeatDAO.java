package org.muffin.muffin.daos;

import javafx.util.Pair;
import lombok.NonNull;
import org.muffin.muffin.beans.Seat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SeatDAO {
    public boolean createSeatsOfTheatre(final int theatreID, @NonNull final Set<Pair<Integer, Integer>> seatsXY);

    public List<Seat> getSeatsOfTheatre(final int theatreID);

    public Optional<Seat> get(final int seatID);

}
