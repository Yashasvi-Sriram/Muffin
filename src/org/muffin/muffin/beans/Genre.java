package org.muffin.muffin.beans;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Genre {
    private int id;
    @NonNull
    private String name;
}
