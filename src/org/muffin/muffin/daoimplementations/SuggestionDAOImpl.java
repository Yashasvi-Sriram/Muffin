package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.SuggestionDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuggestionDAOImpl implements SuggestionDAO {
    @Override
    public List<Movie> getMovies(int muffId, final int offset, int limit) {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT movie.id, movie.owner_id, movie.name, movie.duration FROM movie,movie_suggestion WHERE  movie_suggestion.muff_id = ? AND movie_suggestion.movie_id = movie.id ORDER BY movie_suggestion.rating DESC OFFSET ? LIMIT ?;")) {
            preparedStmt.setInt(1, muffId);
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
    public List<Muff> getMuffs(int muffId, final int offset, int limit) {
        List<Muff> muffs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT muff.id, muff.handle, muff.name, muff.no_approvals, muff.joined_on FROM muff,muff_suggestion WHERE muff_suggestion.id1 = ? AND muff.id = muff_suggestion.id2 ORDER BY muff_suggestion.distance OFFSET ? LIMIT ?;")) {
            preparedStmt.setInt(1, muffId);
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Muff muff = new Muff(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5).toLocalDateTime());
                muffs.add(muff);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return muffs;
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
