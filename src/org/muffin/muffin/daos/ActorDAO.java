package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;


import lombok.NonNull;
import org.muffin.muffin.beans.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActorDAO {
    public List<Actor> search(final String searchKey, final int offset, final int limit);

    public boolean create(final String name);

    public Optional<Actor> get(final String name);

    public Map<Genre, Integer> getGenreMovieHistogram(int actorId);
}
