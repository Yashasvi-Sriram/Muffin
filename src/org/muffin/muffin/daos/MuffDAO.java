package org.muffin.muffin.daos;

import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.beans.Muff;

import java.util.Optional;

public interface MuffDAO {
    public boolean exists(final String handle, final String password);

    public Optional<Muff> get(final String handle);

    public Optional<Muff> get(final int id);
}
