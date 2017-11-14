package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Optional;

public interface GenreDAO {
    public List<Genre> getAll();

    public List<Genre> searchGenresForMovie(final String searchKey, final int movieId, final int offset, final int limit);

    public List<Genre> getByMovie(final int movieId);

    public Optional<Genre> get(final String name);

}
