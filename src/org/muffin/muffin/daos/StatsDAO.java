package org.muffin.muffin.daos;

import javafx.util.Pair;
import org.muffin.muffin.beans.Genre;

import java.util.List;

public interface StatsDAO {
    public float movieratingaverage(int movieId);
    public List<Pair<Integer,Integer>> moviestats(int movieId);
    public List<Pair<Genre,Integer>> actorstats(int actorId);
}
