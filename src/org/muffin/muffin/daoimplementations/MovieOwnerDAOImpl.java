package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daos.MovieOwnerDAO;
import org.muffin.muffin.db.DBConfig;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class MovieOwnerDAOImpl implements MovieOwnerDAO {
    @Override
    public boolean exists(String handle, String password) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT count(*) FROM movie_owner,movie_owner_password WHERE movie_owner.id = movie_owner_password.id AND handle=? AND password=?;")) {
            preparedStmt.setString(1, handle);
            preparedStmt.setString(2, password);
            ResultSet result = preparedStmt.executeQuery();
            result.next();
            return (result.getInt(1) > 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<MovieOwner> get(String handle) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,handle,name,joined_on FROM movie_owner WHERE handle = ?")) {
            preparedStmt.setString(1, handle);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                MovieOwner movieOwner = new MovieOwner(result.getInt(1), result.getString(2), result.getString(3), result.getTimestamp(4).toLocalDateTime());
                return Optional.of(movieOwner);
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<MovieOwner> get(int id) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,handle,name,joined_on FROM movie_owner WHERE id = ?")) {
            preparedStmt.setInt(1, id);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                MovieOwner movieOwner = new MovieOwner(result.getInt(1), result.getString(2), result.getString(3), result.getTimestamp(4).toLocalDateTime());
                return Optional.of(movieOwner);
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
