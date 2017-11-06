package org.muffin.muffin.daos;

import java.util.Optional;

import org.muffin.muffin.beans.CinemaBuildingOwner;


import lombok.NonNull;

public interface CinemaBuildingOwnerDAO {

    public boolean create(final String handle, final String name, final String password);

    public boolean exists(final String handle, final String password);

    public Optional<CinemaBuildingOwner> get(final String handle);

}


