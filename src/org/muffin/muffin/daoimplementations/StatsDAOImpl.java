package org.muffin.muffin.daoimplementations;

import javafx.util.Pair;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.daos.StatsDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatsDAOImpl implements StatsDAO {
    @Override
    public float movieratingaverage(int movieId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT avg(rating) from review where movie_id = ?")) {
            preparedStatement.setInt(1, movieId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getFloat(1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -2;
    }

    public List<Pair<Integer, Integer>> moviestats(int movieId) {
        List<Pair<Integer, Integer>> stats = new ArrayList<>();
        int[] intArray = new int[11];

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT rating from review where movie_id = ?")) {
            preparedStatement.setInt(1, movieId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                intArray[(int) Math.floor(resultSet.getFloat(1))]++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        int temp = 0;
        while (temp < 11) {
            Pair p = new Pair(temp, intArray[temp]);
            stats.add(p);
            temp++;
        }
        return stats;
    }

    public List<Pair<Genre, Integer>> actorstats(int actorId) {
        List<Pair<Genre, Integer>> stats = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT genre.id, genre.name, COUNT(movie_genre_r.movie_id) FROM genre, movie_genre_r,character WHERE character.actor_id = ? and character.movie_id = movie_genre_r.movie_id and movie_genre_r.genre_id = genre.id group by genre.id, genre.name")){
            preparedStmt.setInt(1,actorId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()){
                Genre genre = new Genre(resultSet.getInt(1),resultSet.getString(2));
                Pair<Genre, Integer> p = new Pair(genre,resultSet.getInt(3));
                stats.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
