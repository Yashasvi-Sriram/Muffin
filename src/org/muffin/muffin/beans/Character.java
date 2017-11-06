package org.muffin.muffin.beans;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Character {
    private int id;
    @NonNull
    private String name;
    private int movieId;
    @NonNull
    private Actor actor;
}
