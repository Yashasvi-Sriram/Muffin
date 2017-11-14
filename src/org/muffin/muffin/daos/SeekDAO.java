package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Optional;

public interface SeekDAO {
    public boolean create(final int muffId, final String text, final List<Integer> genreIds);

    public boolean delete(final int seekId);
}
