package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieDAO {
    public List<Movie> getByOwner(final int ownerId);

    public Optional<Movie> get(final String name);

    public boolean create(@NonNull final String name, final int durationInMinutes, final int ownerId);

    public boolean update(final int movieId, final int ownerId, @NonNull final String name, final int duration);

    public boolean delete(final int movieId, int ownerId);
}
