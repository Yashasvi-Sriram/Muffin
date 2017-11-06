package org.muffin.muffin.beans;

import lombok.*;


@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class CinemaBuilding {
    private int id;
    private int ownerId;
    @NonNull
    private String name;
    @NonNull
    private String streetName;
    @NonNull
    private String city;
    @NonNull
    private String state;
    @NonNull
    private String country;
    @NonNull
    private String zip;
}
