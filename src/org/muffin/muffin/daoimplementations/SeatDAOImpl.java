package org.muffin.muffin.daoimplementations;

import javafx.util.Pair;
import org.muffin.muffin.daos.SeatDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}
