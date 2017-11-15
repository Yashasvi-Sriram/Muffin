package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Character;
import org.muffin.muffin.beans.Movie;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CharacterDAO {
    public List<Character> getByMovie(final int movieId, final int movieOwnerId);

    public List<Character> getByMovie(final int movieId);

    public boolean create(final String name, final int movieId, final int movieOwnerId, final int actorId);

    public Optional<Character> get(final String name, final int movieId, final int movieOwnerId, final int actorId);

    public boolean delete(final int characterId, final int movieOwnerId);
}
