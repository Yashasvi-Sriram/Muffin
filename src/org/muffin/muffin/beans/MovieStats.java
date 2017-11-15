package org.muffin.muffin.beans;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class MovieStats {
    List<Genre> genres;
    float averageRating;
    int userCount;
    Map<Integer, Integer> ratingHistogram;

}
