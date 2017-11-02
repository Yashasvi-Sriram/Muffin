package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Character;
import java.util.List;


public interface CharacterDAO {
    public List<Character> getByMovie(final int movieId);
}
