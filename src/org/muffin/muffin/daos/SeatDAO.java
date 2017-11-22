package org.muffin.muffin.daos;

import java.util.List;

public interface SeatDAO {
    public boolean createList(int theatreID, List<Integer> x, List<Integer> y);
}
