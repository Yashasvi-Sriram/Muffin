package org.muffin.muffin.daos;

import lombok.NonNull;

import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;

import java.util.List;
import java.util.Optional;

public interface CinemaBuildingDAO {
    

    public boolean create(final int ownerId, @NonNull final String name, @NonNull final String streetName, @NonNull final String city, @NonNull final String state,  @NonNull final String country, @NonNull final String zip);

    public Optional<CinemaBuilding> get(@NonNull final String name, @NonNull final String streetName, @NonNull final String city, @NonNull final String state,  @NonNull final String country, @NonNull final String zip);
}
