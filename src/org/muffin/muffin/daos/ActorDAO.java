package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;

import java.util.Optional;

public interface ActorDAO {
    public Optional<Actor> search(final String searchkey);
}
