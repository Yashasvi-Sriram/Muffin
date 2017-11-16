package org.muffin.muffin.beans;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class SeekResponse {
    private int id;
    private int muffId;
    private int seekId;
    private int movieId;
    @NonNull
    private String text;
    private LocalDateTime timestamp;
}
