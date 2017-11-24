package org.muffin.muffin.daos;

import lombok.NonNull;
import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.beans.Muff;

import java.util.List;
import java.util.Optional;

public interface MuffDAO {
    public Optional<Muff> create(final String handle, final String name, final String password);

    public boolean exists(final String handle, final String password);

    public Optional<Muff> get(final String handle);

    public Optional<Muff> get(final int id);

    public List<Muff> search(final String searchKey, final int offset, final int limit);

    public List<Muff> getFollowees(final int muffId);

    public List<Muff> getFollowers(final int muffId);

    public Optional<Boolean> toggleFollows(final int muffId, final int followeeId);

    public Optional<Boolean> doesFollows(final int muffId, final int followeeId);

    public boolean incrementNoApprovalsByOne(final int muffId);

    public boolean decrementNoApprovalsByOne(final int muffId);
}
