package org.muffin.muffin.daos;

import javafx.util.Pair;
import lombok.NonNull;

import java.util.Set;

public interface SeatDAO {
    public boolean createSeatsOfTheatre(int theatreID, @NonNull Set<Pair<Integer, Integer>> seatsXY);
}
