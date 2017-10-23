package org.muffin.muffin.beans;

import lombok.*;

@ToString
@EqualsAndHashCode(of = {"id"})
@Getter
@AllArgsConstructor
public class MovieOwner {
    private int id;
    @NonNull
    private String handle;
    @NonNull
    private String name;
    @NonNull
    private String password;
}
