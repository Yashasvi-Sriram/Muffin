package org.muffin.muffin.daoimplementations;

import javafx.util.Pair;
import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Seat;
import org.muffin.muffin.daos.SeatDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SeatDAOImpl implements SeatDAO {
    @Override
    public boolean createSeatsOfTheatre(int theatreID, Set<Pair<Integer, Integer>> seatsXY) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);) {
            PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO seat(theatre_id,x,y) VALUES(?,?,?);");
            conn.setAutoCommit(false);
            for (Pair<Integer, Integer> seatXY : seatsXY) {
                preparedStmt.setInt(1, theatreID);
                preparedStmt.setInt(2, seatXY.getKey());
                preparedStmt.setInt(3, seatXY.getValue());
                preparedStmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Seat> getSeatsOfTheatre(int theatreID) {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, theatre_id, x, y FROM seat WHERE theatre_id = ?")) {
            preparedStmt.setInt(1, theatreID);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Seat seat = new Seat(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4)
                );
                seats.add(seat);
            }
            return seats;
        } catch (SQLException e) {
            e.printStackTrace();
            return seats;
        }
    }

    @Override
    public Optional<Seat> get(int seatID) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, theatre_id, x, y FROM seat WHERE id = ?")) {
            preparedStmt.setInt(1, seatID);
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {
                Seat seat = new Seat(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4)
                );

                return Optional.of(seat);

            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();

        }

    }


}
