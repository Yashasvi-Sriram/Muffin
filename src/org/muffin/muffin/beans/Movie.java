package org.muffin.muffin.beans;

import lombok.*;

import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Movie {
    private int id;
    private int movieOwnerId;
    @NonNull
    private String name;
    private int durationInMinutes;
    @NonNull
    private List<Genre> genres;
}
