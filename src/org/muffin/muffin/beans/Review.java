package org.muffin.muffin.beans;

import java.time.LocalDateTime;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Review {
    private int id;
    private int movieId;
    @NonNull
    private String movieName;
    private int muffId;
    @NonNull
    private String muffName;
    @NonNull
    private String muffHandle;
    private float rating;
    @NonNull
    private String text;
    @NonNull
    private LocalDateTime addedOn;
}