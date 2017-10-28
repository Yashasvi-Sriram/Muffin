package org.muffin.muffin.daos;

public interface MovieOwnerDAO {
    public boolean exists(final String handle, final String password);
}
