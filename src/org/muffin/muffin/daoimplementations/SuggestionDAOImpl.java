package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.SuggestionDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SuggestionDAOImpl implements SuggestionDAO {
    @Override
    public List<Movie> getMovies(int muffId) {
        return new ArrayList<>();
    }

    @Override
    public List<Actor> getActors(int muffId) {
        return new ArrayList<>();
    }

    @Override
    public List<Muff> getMuffs(int muffId) {
        return new ArrayList<>();
    }
}
