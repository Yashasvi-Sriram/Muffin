package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Seek {
    private int id;
    @NonNull
    private Muff muff;
    @NonNull
    private String text;
    private LocalDateTime timestamp;
    @NonNull
    private List<Genre> genres;
}
