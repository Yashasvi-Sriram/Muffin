package org.muffin.muffin.beans;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Seat {
    private int id;
    private int theatreId;
    private int x;
    private int y;
}
