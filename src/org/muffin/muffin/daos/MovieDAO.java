package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Movie;

import java.util.List;

public interface MovieDAO {
    public List<Movie> getByOwner(final int ownerId);

    public boolean create(@NonNull final String name, final int durationInMinutes, final int ownerId);
}
