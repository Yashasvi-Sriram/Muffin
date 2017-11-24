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
    private String movieName;
    @NonNull
    private LocalDateTime bookedOn;
    @NonNull
    private List<Seat> bookedShowSeats;
}
