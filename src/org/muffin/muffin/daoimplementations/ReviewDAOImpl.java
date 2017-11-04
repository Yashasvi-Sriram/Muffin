package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Review;
import org.muffin.muffin.daos.ReviewDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, movie_id, movie.name, muff_id, muff.name ,rating,text, timestamp FROM (review NATURAL JOIN movie) NATURAL JOIN muff WHERE movie_id = ? and muff_id = ?")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, muffId);

            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Review review = new Review(result.getInt(1), result.getInt(2), result.getString(3), result.getInt(4), result.getString(5), result.getFloat(6), result.getString(7), result.getTimestamp(8).toLocalDateTime());
                return Optional.of(review);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getbymovie(int movieId) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, movie_id, movie.name, muff_id, muff.name ,rating,text, timestamp FROM (review NATURAL JOIN movie) NATURAL JOIN muff WHERE movie_id = ?")) {
            preparedStmt.setInt(1, movieId);

            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Review review = new Review(result.getInt(1), result.getInt(2), result.getString(3), result.getInt(4), result.getString(5), result.getFloat(6), result.getString(7), result.getTimestamp(8).toLocalDateTime());
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> getbyuser(int userId) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, movie_id, movie.name, muff_id, muff.name ,rating,text, timestamp FROM (review NATURAL JOIN movie) NATURAL JOIN muff WHERE muff_id = ?")) {
            preparedStmt.setInt(1, userId);

            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Review review = new Review(result.getInt(1), result.getInt(2), result.getString(3), result.getInt(4), result.getString(5), result.getFloat(6), result.getString(7), result.getTimestamp(8).toLocalDateTime());
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }


    @Override
    public boolean update(int id, float rating, String text) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("UPDATE review set rating = ? and text = ? where id = ?")) {
            preparedStmt.setFloat(1,rating);
            preparedStmt.setString(2,text);
            preparedStmt.setInt(3,id);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE from review where id = ?")) {
            preparedStmt.setInt(1,id);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
