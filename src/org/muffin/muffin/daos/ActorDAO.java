package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;


import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface ActorDAO {
    public List<Actor> search(final String searchKey);
    public boolean create(@NonNull final String name);
    public Optional<Actor> get(final String name);
}
