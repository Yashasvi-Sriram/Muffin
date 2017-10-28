package org.muffin.muffin.daos;

import org.muffin.muffin.beans.MovieOwner;

import java.util.Optional;

public interface MovieOwnerDAO {
    public boolean exists(final String handle, final String password);

    public Optional<MovieOwner> get(final String handle);
}
