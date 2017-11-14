package org.muffin.muffin.daoimplementations;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.Genre;


import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.db.DBConfig;


public class GenreDAOImpl implements GenreDAO {
    @Override
    public List<Genre> getGenre(String searchKey, final int offset, final int limit) {
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM genre WHERE name ILIKE ? ORDER BY name OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);

            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Genre genre = new Genre(resultSet.getInt(1), resultSet.getString(2));
                genres.add(genre);
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }


        return genres;
    }

    public List<Genre> getGenreForMovie(String searchKey, int movieId, final int offset, final int limit) {
        List<Genre> genres = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM genre WHERE name ILIKE ? and id not in (select genre_id from movie_genre_r where movie_id = ?) ORDER BY name OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            preparedStmt.setInt(2, movieId);
            preparedStmt.setInt(3, offset);
            preparedStmt.setInt(4, limit);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Genre genre = new Genre(resultSet.getInt(1), resultSet.getString(2));
                genres.add(genre);
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }


        return genres;
    }

    @Override
    public List<Genre> getByMovie(int movieId) {

        List<Genre> genres = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT genre.id,genre.name FROM genre,movie_genre_r WHERE  movie_genre_r.genre_id = genre.id and movie_genre_r.movie_id = ?")) {
            preparedStmt.setInt(1, movieId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Genre genre = new Genre(resultSet.getInt(1), resultSet.getString(2));
                genres.add(genre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return genres;

    }

    @Override
    public Optional<Genre> get(String name) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, name  FROM genre WHERE name = ?")) {
            preparedStmt.setString(1, name);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Genre genre = new Genre(result.getInt(1), result.getString(2));
                return Optional.of(genre);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}