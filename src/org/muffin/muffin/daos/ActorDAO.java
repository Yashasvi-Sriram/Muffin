package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;


import lombok.NonNull;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActorDAO {
    public List<Actor> search(final String searchKey, final int offset, final int limit);

    public Optional<Actor> create(final String name);

    public Optional<Actor> get(final String name);

    public Optional<Actor> get(final int id);

    public Map<Genre, Integer> getGenreMovieHistogram(int actorId);

    public Optional<Integer> getLikeCount(int actorId);

    public Map<Integer, String> getAllMovies(final int actorId);

    public Optional<Boolean> toggleLikes(final int muffId, final int actorId);

    public Optional<Boolean> doesLikes(final int muffId, final int actorId);
}
