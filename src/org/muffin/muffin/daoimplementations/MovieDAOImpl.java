package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieDAOImpl implements MovieDAO {
    @Override
    public List<Movie> getByOwner(int ownerId) {
        List<Movie> movieList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM movie WHERE movie.movie_owner_id = ?")) {
            preparedStmt.setInt(1, ownerId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Movie movie = new Movie(result.getInt(1), result.getInt(3), result.getString(2), result.getInt(4));
                movieList.add(movie);
            }
            return movieList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean create(String name, int durationInMinutes, int ownerId) {
        return false;
    }

}
