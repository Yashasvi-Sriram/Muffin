package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Show;
import org.muffin.muffin.beans.Showtime;
import org.muffin.muffin.daos.ShowDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.StringTokenizer;

public class ShowDAOImpl implements ShowDAO {
    @Override
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT theatre.id from theatre,cinema_building where theatre.id = ? and cinema_building_id = cinema_building.id and owner_id = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO show(theatre_id,movie_id,during) VALUES (?,?,?);")) {
            verifyStmt.setInt(1, theatreId);
            verifyStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()) {
                insertStmt.setInt(1, theatreId);
                insertStmt.setInt(2, movieId);
                String tsrange = "[" + showtime.getStartTime().toString() + "," + showtime.getEndTime().toString() + "]";
                insertStmt.setString(3, tsrange);
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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during from show where theatre_id = ?  and movie_id = ? and during = ?")) {
            preparedStmt.setInt(1, theatreId);
            preparedStmt.setInt(2, movieId);
            String tsrange = "[" + showtime.getStartTime().toString() + "," + showtime.getEndTime().toString() + "]";
            preparedStmt.setString(3, tsrange);
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {
                StringTokenizer tokens = new StringTokenizer(resultSet.getString(4),",");
                LocalDateTime startTime = LocalDateTime.parse(tokens.nextToken().substring(1));
                String helper = tokens.nextToken();
                helper = helper.substring(0,helper.length()-1);
                LocalDateTime endTime = LocalDateTime.parse(helper);
                Showtime showtime1 = new Showtime(startTime,endTime);
                Show show = new Show(resultSet.getInt(1),resultSet.getInt(2),resultSet.getInt(3),showtime1);
                return Optional.of(show);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public boolean delete(int showId, int cinemaBuildingOwnerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT show.id from show,theatre,cinema_building where show.id = ? and show.theatre_id = thetre.id and cinema_building_id = cinema_building.id and owner_id = ?");
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM show WHERE id = ?")) {
            verifyStmt.setInt(1,showId);
            verifyStmt.setInt(2,cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()){
                deleteStmt.setInt(1,showId);
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
