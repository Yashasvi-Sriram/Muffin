package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Showtime {
    @NonNull
    private LocalDateTime startTime;
    @NonNull
    private LocalDateTime endTime;
}
