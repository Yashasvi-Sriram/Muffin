package org.muffin.muffin.beans;

import lombok.*;


@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class ValidRegion {
    private int id;
    @NonNull
    private String city;
    @NonNull
    private String state;
    @NonNull
    private String country;

}
