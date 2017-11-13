package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Review;
import org.muffin.muffin.daos.ReviewDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public boolean create(int movieId, int muffId, float rating, String text) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO review(movie_id,muff_id,rating,text) VALUES (?,?,?,?);")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, muffId);
            preparedStmt.setFloat(3, rating);
            preparedStmt.setString(4, text);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Review> get(int movieId, int muffId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT  review.id,  review.rating,  review.text,  review.timestamp, movie.id, movie.name,  muff.id,  muff.handle, muff.name, muff.level, muff.joined_on  FROM review, movie, muff WHERE movie_id = ? AND muff_id = ? AND review.movie_id = movie.id AND review.muff_id = muff.id;")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, muffId);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Review review = new Review(
                        result.getInt(1),
                        result.getFloat(2),
                        result.getString(3),
                        result.getTimestamp(4).toLocalDateTime(),
                        result.getInt(5),
                        result.getString(6),
                        new Muff(result.getInt(7),
                                result.getString(8),
                                result.getString(9),
                                result.getInt(10),
                                result.getTimestamp(11).toLocalDateTime()));
                return Optional.of(review);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getByMovie(int movieId, int offset, int limit) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT  review.id,  review.rating,  review.text,  review.timestamp, movie.id, movie.name,  muff.id,  muff.handle, muff.name, muff.level, muff.joined_on FROM review, movie, muff WHERE movie_id = ? AND review.movie_id = movie.id AND review.muff_id = muff.id ORDER BY review.timestamp OFFSET ? LIMIT ?")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet result = preparedStmt.executeQuery();
            resultSetConverter(reviews, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> getByMuff(int muffId, int offset, int limit) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT  review.id,  review.rating,  review.text,  review.timestamp, movie.id, movie.name,  muff.id,  muff.handle, muff.name, muff.level, muff.joined_on FROM review, movie, muff WHERE muff_id = ? AND review.movie_id = movie.id AND review.muff_id = muff.id ORDER BY review.timestamp OFFSET ? LIMIT ?")) {
            preparedStmt.setInt(1, muffId);
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet result = preparedStmt.executeQuery();
            resultSetConverter(reviews, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> getByFollowers(int muffId, int offset, int limit) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT  review.id,  review.rating,  review.text,  review.timestamp, movie.id, movie.name,  muff.id,  muff.handle, muff.name, muff.level, muff.joined_on FROM review, movie, muff, follows  WHERE muff.id = ? AND muff.id = follows.id1 AND review.movie_id = movie.id AND review.muff_id = follows.id2 ORDER BY review.timestamp OFFSET ? LIMIT ?")) {
            preparedStmt.setInt(1, muffId);
            preparedStmt.setInt(2, offset);
            preparedStmt.setInt(3, limit);
            ResultSet result = preparedStmt.executeQuery();
            resultSetConverter(reviews, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public boolean update(int movieId, int muffId, float rating, String text) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("UPDATE review SET rating = ?, text = ?, timestamp = CURRENT_TIMESTAMP WHERE movie_id = ? AND muff_id = ?")) {
            preparedStmt.setFloat(1, rating);
            preparedStmt.setString(2, text);
            preparedStmt.setInt(3, movieId);
            preparedStmt.setInt(4, muffId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int movieId, int muffId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM review WHERE movie_id = ? AND muff_id = ?")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, muffId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void resultSetConverter(List<Review> reviews, ResultSet result) {
        try {
            while (result.next()) {
                Review review = new Review(
                        result.getInt(1),
                        result.getFloat(2),
                        result.getString(3),
                        result.getTimestamp(4).toLocalDateTime(),
                        result.getInt(5),
                        result.getString(6),
                        new Muff(result.getInt(7),
                                result.getString(8),
                                result.getString(9),
                                result.getInt(10),
                                result.getTimestamp(11).toLocalDateTime()));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
