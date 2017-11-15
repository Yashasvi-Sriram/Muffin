package org.muffin.muffin.beans;

import java.time.LocalDateTime;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Review {
    private int id;
    private float rating;
    @NonNull
    private String text;
    @NonNull
    private LocalDateTime lastModified;
    private int movieId;
    @NonNull
    private String movieName;
    @NonNull
    private Muff muff;
}