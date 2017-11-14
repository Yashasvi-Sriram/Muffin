package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MovieDAOImpl implements MovieDAO {
    @Override
    public List<Movie> getByOwner(int ownerId) {
        List<Movie> movieList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM movie WHERE movie.owner_id = ?")) {
            preparedStmt.setInt(1, ownerId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                List<Genre> genres = getGenreList(result.getInt(1), conn);
                Movie movie = new Movie(result.getInt(1), result.getInt(3), result.getString(2), result.getInt(4), genres);
                movieList.add(movie);
            }
            return movieList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    @Override
    public List<Movie> getByGenre(int genreId) {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT movie.* FROM movie,movie_genre WHERE movie.id = movie_genre.movieId AND genreId = ?")) {
            preparedStmt.setInt(1, genreId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                List<Genre> genres = getGenreList(result.getInt(1), conn);
                Movie movie = new Movie(result.getInt(1), result.getInt(2), result.getString(3), result.getInt(4), genres);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    @Override
    public Optional<Movie> get(String name) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, owner_id, name, duration FROM movie WHERE name = ?")) {
            preparedStmt.setString(1, name);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                List<Genre> genres = getGenreList(result.getInt(1), conn);
                Movie movie = new Movie(result.getInt(1), result.getInt(2), result.getString(3), result.getInt(4), genres);
                return Optional.of(movie);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Movie> search(String substring, final int offset, final int limit) {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, owner_id, name, duration FROM movie WHERE name ILIKE ?  ORDER BY name OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + substring + "%");
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                int movieId = rs.getInt(1);
                List<Genre> genres = getGenreList(movieId, conn);
                Movie movie = new Movie(movieId, rs.getInt(2), rs.getString(3), rs.getInt(4), genres);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public boolean create(String name, int durationInMinutes, int ownerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO movie(name,owner_id,duration) VALUES (?,?,?);")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, ownerId);
            preparedStmt.setInt(3, durationInMinutes);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(int movieId, int ownerId, String name, int duration) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("UPDATE movie SET name=?, duration=? WHERE id = ? AND owner_id = ?;")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, duration);
            preparedStmt.setInt(3, movieId);
            preparedStmt.setInt(4, ownerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateGenre(int movieId, int ownerId, int genreId, int flag) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt1 = conn.prepareStatement("INSERT INTO movie_genre(movie_id,genre_id) SELECT id,? FROM movie WHERE id = ? AND owner_id = ?");
             PreparedStatement preparedStmt2 = conn.prepareStatement("DELETE FROM movie_genre WHERE genre_id = ? AND movie_id = ? AND EXISTS (SELECT * FROM movie WHERE id = ? AND owner_id = ?)")) {
            if (flag == 1) {
                preparedStmt1.setInt(1, genreId);
                preparedStmt1.setInt(2, movieId);
                preparedStmt1.setInt(3, ownerId);
                int result = preparedStmt1.executeUpdate();
                return result == 1;
            } else if (flag == 0) {
                preparedStmt2.setInt(1, genreId);
                preparedStmt2.setInt(2, movieId);
                preparedStmt2.setInt(3, movieId);
                preparedStmt2.setInt(4, ownerId);
                int result = preparedStmt2.executeUpdate();
                return result == 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int movieId, int ownerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM movie WHERE id = ? AND owner_id = ?;")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, ownerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Genre> getGenreList(int movieId, Connection conn) {
        List<Genre> genres = new ArrayList<>();
        try (PreparedStatement preparedStmt = conn.prepareStatement("SELECT genre.id, genre.name FROM genre, movie_genre_r WHERE movie_id = ? AND genre.id = genre_id")) {
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
}
