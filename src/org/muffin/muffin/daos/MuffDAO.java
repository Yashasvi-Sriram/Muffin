package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.beans.Muff;

import java.util.List;
import java.util.Optional;

public interface MuffDAO {
    public boolean create(@NonNull final String handle, @NonNull final String name, @NonNull final String password);

    public boolean exists(final String handle, final String password);

    public Optional<Muff> get(final String handle);

    public Optional<Muff> get(final int id);

    public List<Muff> search(final String searchKey);
}
