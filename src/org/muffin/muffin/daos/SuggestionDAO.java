package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;

import java.util.List;

public interface SuggestionDAO {
    public List<Movie> getMovies(final int muffId);

    public List<Actor> getActors(final int muffId);

    public List<Muff> getMuffs(final int muffId);
}
