package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Show;
import org.muffin.muffin.beans.Showtime;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.ShowDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.Object;
import java.time.format.DateTimeFormatter;

public class ShowDAOImpl implements ShowDAO {
    private String toTSRange(Showtime showtime) {
        return "[" + showtime.getStartTime().toString() + "," + showtime.getEndTime().toString() + "]";
    }

    @Override
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT theatre.id FROM theatre,cinema_building WHERE theatre.id = ? AND cinema_building_id = cinema_building.id AND owner_id = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO show(theatre_id,movie_id,during) VALUES (?,?,?::tsrange);")) {
            verifyStmt.setInt(1, theatreId);
            verifyStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()) {
                insertStmt.setInt(1, theatreId);
                insertStmt.setInt(2, movieId);
                insertStmt.setString(3, toTSRange(showtime));
                int result = insertStmt.executeUpdate();
                return result == 1;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Show> get(int movieId, int theatreId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during FROM show WHERE theatre_id = ?  AND movie_id = ? AND during = ?::tsrange")) {
            preparedStmt.setInt(1, theatreId);
            preparedStmt.setInt(2, movieId);
            preparedStmt.setString(3, toTSRange(showtime));
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {
                StringTokenizer tokens = new StringTokenizer(resultSet.getString(4), ",");
                String initial = tokens.nextToken();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(initial.substring(2, initial.length() - 1), formatter);
                String helper = tokens.nextToken();
                helper = helper.substring(1, helper.length() - 2);
                LocalDateTime endTime = LocalDateTime.parse(helper, formatter);
                Showtime showtime1 = new Showtime(startTime, endTime);
                MovieDAO movieDAO = new MovieDAOImpl();
                Optional<Movie> movieOpt = movieDAO.get(resultSet.getInt(3));
                if (movieOpt.isPresent()) {
                    Show show = new Show(resultSet.getInt(1), resultSet.getInt(2), movieOpt.get(), showtime1);
                    return Optional.of(show);
                }
                return Optional.empty();
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public List<Show> get(int theatreId) {
        List<Show> showList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during FROM show WHERE show.theatre_id = ?")) {
            preparedStmt.setInt(1, theatreId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                StringTokenizer tokens = new StringTokenizer(result.getString(4), ",");
                String initial = tokens.nextToken();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(initial.substring(2, initial.length() - 1), formatter);
                String helper = tokens.nextToken();
                helper = helper.substring(1, helper.length() - 2);
                LocalDateTime endTime = LocalDateTime.parse(helper, formatter);
                Showtime showtime1 = new Showtime(startTime, endTime);
                MovieDAO movieDAO = new MovieDAOImpl();
                Optional<Movie> movieOpt = movieDAO.get(result.getInt(3));
                if (movieOpt.isPresent()) {
                    Show show = new Show(result.getInt(1), result.getInt(2), movieOpt.get(), showtime1);
                    showList.add(show);
                }

            }
            return showList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean delete(int showId, int cinemaBuildingOwnerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT show.id FROM show,theatre,cinema_building WHERE show.id = ? AND show.theatre_id = theatre.id AND cinema_building_id = cinema_building.id AND owner_id = ?");
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM show WHERE id = ?")) {
            verifyStmt.setInt(1, showId);
            verifyStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()) {
                deleteStmt.setInt(1, showId);
                int result = deleteStmt.executeUpdate();
                return result == 1;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}
