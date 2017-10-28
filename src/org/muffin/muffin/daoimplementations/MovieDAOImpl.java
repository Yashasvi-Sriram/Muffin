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
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT into movie(name,movie_owner_id,duration) VALUES (?,?,?);")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, ownerId);
            preparedStmt.setInt(3, durationInMinutes);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateName(int movieID, int ownerID, String name) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("UPDATE movie SET name=? where id = ? and movie_owner_id = ?;")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, movieID);
            preparedStmt.setInt(3, ownerID);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDuration(int movieID, int ownerID, int duration) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("UPDATE movie SET duration = ? where id = ? and movie_owner_id = ?;")) {
            preparedStmt.setInt(1, duration);
            preparedStmt.setInt(2, movieID);
            preparedStmt.setInt(3, ownerID);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
