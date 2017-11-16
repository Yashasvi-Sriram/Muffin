package org.muffin.muffin.daos;


import lombok.NonNull;
import org.muffin.muffin.beans.Theatre;

import java.util.List;
import java.util.Optional;

public interface TheatreDAO {
    public List<Theatre> getByCinemaBuilding(final int cinemaBuildingId, final int cinemaBuildingOwnerId);

    public Optional<Theatre> create(final int cinemaBuildingId, final int screenNo, final int cinemaBuildingOwnerId);

    public Optional<Theatre> get(final int cinemaBuildingId, final int screenNo);

    public boolean delete(final int theatreId, final int cinemaBuildingOwnerId);
}

