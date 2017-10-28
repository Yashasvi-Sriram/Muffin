package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Movie;

import java.util.List;

public interface MovieDAO {
    public List<Movie> getByOwner(String ownerHandle);
}
