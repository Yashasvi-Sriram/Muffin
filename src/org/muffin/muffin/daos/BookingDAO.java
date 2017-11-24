package org.muffin.muffin.daos;

import javafx.util.Pair;
import lombok.NonNull;
import org.muffin.muffin.beans.Booking;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingDAO {

    public boolean create(final int showId, final int muffId, final List<Seat> bookedShowSeats);

    public List<Seat> getBookedSeats(final int showId);

    public List<Booking> getMuffBookingHistory(final int muffId);

}
