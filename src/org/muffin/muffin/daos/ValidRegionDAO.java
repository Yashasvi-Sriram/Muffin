package org.muffin.muffin.daos;

import org.muffin.muffin.beans.ValidRegion;


import lombok.NonNull;


import java.util.List;
import java.util.Optional;

public interface ValidRegionDAO {
    public List<ValidRegion> search(final String searchKey, final int offset, final int limit);

    public Optional<ValidRegion> get(final String city, final String state, final String country);
}
