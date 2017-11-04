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
    private int actorId;
    @NonNull
    private String actorName;
}
