package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Booking {
    private int id;
    private int showId;
    private int muffId;
    @NonNull
    private LocalDateTime bookedOn;
    private List<Seat> bookedShowSeats;
}
