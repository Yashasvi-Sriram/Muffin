package org.muffin.muffin.daoimplementations;

import lombok.NonNull;
import org.muffin.muffin.daos.MovieOwnerDAO;

public class MovieOwnerDAOImpl implements MovieOwnerDAO {
    @Override
    public boolean exists(String handle, String password) {
        return false;
    }
}
