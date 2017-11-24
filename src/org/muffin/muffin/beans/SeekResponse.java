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
    @NonNull
    private Muff muff;
    private int seekId;
    private int movieId;
    @NonNull
    private String movieName;
    @NonNull
    private String text;
    private int approvalStatus;
    private LocalDateTime timestamp;
}
