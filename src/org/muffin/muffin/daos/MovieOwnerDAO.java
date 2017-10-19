package org.muffin.muffin.daos;

import lombok.NonNull;

public interface MovieOwnerDAO {
    public void create(@NonNull final String handle, @NonNull final String name, @NonNull final String password);
}
