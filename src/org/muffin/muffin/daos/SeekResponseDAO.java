package org.muffin.muffin.daos;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seek;
import org.muffin.muffin.beans.SeekResponse;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SeekResponseDAO {
    public Optional<SeekResponse> create(final int muffId, final int seekId, final int movieId, final String text);

    public List<SeekResponse> getBySeek(final int seekId, final int offset, final int limit, final Timestamp lastSeen);

    public List<Integer> getMuffIdsOfAllSeekResponsesOfSeek(final int seekId);

    public Optional<Boolean> checkForNewResponsesOfSeek(final int seekId, final Timestamp lastSeen);

    public Optional<Integer> toggleApproval(final int seekResponseId);

    public Optional<SeekResponse> getById(int seekId);
}
