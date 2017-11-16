package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Seek;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface SeekDAO {
    public boolean create(final int muffId, final String text, final List<Integer> genreIds);

    public boolean delete(final int seekId);

    public List<Seek> getByMuff(final int muffId, final int offset, final int limit, final Timestamp lastSeen);

    public List<Seek> getByFollowers(final int muffId, final int offset, final int limit, final Timestamp lastSeen);
}
