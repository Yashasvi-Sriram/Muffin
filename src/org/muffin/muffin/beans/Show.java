package org.muffin.muffin.beans;

import lombok.*;

import java.awt.font.NumericShaper.*;
import java.sql.Timestamp;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Show {
    private int id;
    private int theatreId;
    private Movie movie;
    @NonNull
    private Showtime showtime;
}
