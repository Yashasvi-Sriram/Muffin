package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieDAO {
    public List<Movie> getByOwner(final int ownerId);

    public List<Genre> getGenre(final String substring);

    public List<Movie> getByGenre(final int genreId);

    public Optional<Movie> get(final String name);

    public List<Movie> search(final String substring, final int offset, final int limit);

    public boolean create(final String name, final int durationInMinutes, final int ownerId);

    public boolean update(final int movieId, final int ownerId, final String name, final int duration);

    public boolean updateGenre(final int movieId, final int ownerId, final int genre, final int flag);

    public boolean delete(final int movieId, int ownerId);
}
