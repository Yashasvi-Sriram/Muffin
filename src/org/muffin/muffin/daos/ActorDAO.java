package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorDAO {
    public List<Actor> search(final String searchkey);
}
