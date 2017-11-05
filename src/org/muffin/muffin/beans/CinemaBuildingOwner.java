package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class CinemaBuildingOwner {
    private int id;
    @NonNull
    private String handle;
    @NonNull
    private String name;
    @NonNull
    private LocalDateTime joinedOn;
}
