package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Muff {
    private int id;
    @NonNull
    private String handle;
    @NonNull
    private String name;
    private int level;
    @NonNull
    private LocalDateTime joinedOn;
}
