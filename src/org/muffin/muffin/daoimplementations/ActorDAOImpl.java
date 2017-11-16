package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.*;

public class ActorDAOImpl implements ActorDAO {

    @Override
    public List<Actor> search(String searchKey, final int offset, final int limit) {
        List<Actor> actorList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM actor WHERE name ILIKE ? ORDER BY name OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Actor actor = new Actor(resultSet.getInt(1), resultSet.getString(2));
                actorList.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return actorList;
    }

    @Override
    public Optional<Actor> create(String name) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO actor(name) VALUES (?) RETURNING id, name;")) {
            preparedStmt.setString(1, name);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Actor actor = new Actor(result.getInt(1), result.getString(2));
                return Optional.of(actor);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Actor> get(String name) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, name FROM actor WHERE name = ?")) {
            preparedStmt.setString(1, name);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Actor actor = new Actor(result.getInt(1), result.getString(2));
                return Optional.of(actor);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Actor> get(int id) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, name FROM actor WHERE id = ?")) {
            preparedStmt.setInt(1, id);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Actor actor = new Actor(result.getInt(1), result.getString(2));
                return Optional.of(actor);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public Map<Genre, Integer> getGenreMovieHistogram(int actorId) {
        Map<Genre, Integer> stats = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT genre.id, genre.name, COUNT(movie_genre_r.movie_id) FROM genre, movie_genre_r,character WHERE character.actor_id = ? AND character.movie_id = movie_genre_r.movie_id AND movie_genre_r.genre_id = genre.id GROUP BY genre.id")) {
            preparedStmt.setInt(1, actorId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Genre genre = new Genre(resultSet.getInt(1), resultSet.getString(2));
                stats.put(genre, resultSet.getInt(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public Optional<Integer> getLikeCount(int actorId) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT count(muff_id) FROM muff_likes_actor,actor WHERE actor.id = ? AND muff_likes_actor.actor_id = actor.id")) {
            preparedStatement.setInt(1, actorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Map<Integer, String> getAllMovies(int actorId) {

        Map<Integer, String> movieMap = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT movie.id,movie.name FROM movie,character WHERE  character.movie_id = movie.id AND character.actor_id = ?")) {
            preparedStmt.setInt(1, actorId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                movieMap.put(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieMap;

    }

    @Override
    public Optional<Boolean> toggleLikes(int muffId, int actorId) {
        // One db connection opens and closes here
        Optional<Boolean> doesLikes = doesLikes(muffId, actorId);
        if (doesLikes.isPresent()) {
            try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
                 PreparedStatement insert = conn.prepareStatement("INSERT INTO muff_likes_actor(muff_id, actor_id) VALUES (?,?)");
                 PreparedStatement delete = conn.prepareStatement("DELETE FROM muff_likes_actor WHERE (muff_id, actor_id) = (?,?)")) {
                if (doesLikes.get()) {
                    delete.setInt(2, actorId);
                    delete.setInt(1, muffId);
                    int result = delete.executeUpdate();
                    return result == 1 ? Optional.of(false) : Optional.empty();
                } else {
                    insert.setInt(1, muffId);
                    insert.setInt(2, actorId);
                    int result = insert.executeUpdate();
                    return result == 1 ? Optional.of(true) : Optional.empty();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> doesLikes(int muffId, int actorId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT count(*) FROM muff_likes_actor WHERE muff_likes_actor.muff_id = ? AND muff_likes_actor.actor_id = ?")) {
            preparedStmt.setInt(2, actorId);
            preparedStmt.setInt(1, muffId);
            ResultSet resultSet = preparedStmt.executeQuery();
            resultSet.next();
            return Optional.of(resultSet.getInt(1) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
