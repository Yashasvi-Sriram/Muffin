package org.muffin.muffin.beans;

import lombok.*;


@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Theatre {
    private int id;
    private int cinemaBuildingId;
    private int screenNo;
}

