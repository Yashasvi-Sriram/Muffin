package org.muffin.muffin.daoimplementations;

import javafx.util.Pair;
import org.muffin.muffin.beans.*;
import org.muffin.muffin.daos.BookingDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BookingDAOImpl implements BookingDAO {
    @Override
    public boolean create(int showId, int muffId, List<Seat> bookedShowSeats) {

//        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);) {
//            try {
//                conn.setAutoCommit(false);
//                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//                PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO booking(show_id,muff_id) RETURNING id");
//                PreparedStatement preparedStmt2 = conn.prepareStatement("INSERT INTO booked_show_seats(theatre_id,seat_id,show_id,booking_id) VALUES(?,?,?,?)");
//                preparedStmt.setInt(1, showId);
//                preparedStmt.setInt(2, muffId);
//                ResultSet result = preparedStmt.executeQuery();
//                if (result.next()) {
//                    int bookingId = result.getInt(1);
//                    for (int i = 0; i < bookedShowSeats.size(); i++) {
//                        Seat seat = bookedShowSeats.get(i);
//                        preparedStmt2.setInt(1, seat.getTheatreId());
//                        preparedStmt2.setInt(2, seat.getId());
//                        preparedStmt2.setInt(3, showId);
//                        preparedStmt2.setInt(4, bookingId);
//                        preparedStmt2.executeQuery();
//                    }
//                    conn.commit();
//                    return true;
//                } else {
//                    conn.rollback();
//                    return false;
//                }
//            } catch (Exception e) {
//                conn.rollback();
//                return false;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
        return false;
    }

    @Override
    public List<Seat> getBookedSeats(final int showId) {
//        List<Seat> seatList = new ArrayList<>();
//        SeatDAO seatDAO = new SeatDAOImpl();
//        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
//             PreparedStatement preparedStmt = conn.prepareStatement("SELECT seat_id FROM booked_show_seats WHERE show_id = ?")) {
//            preparedStmt.setInt(1, showId);
//            ResultSet resultSet = preparedStmt.executeQuery();
//            while (resultSet.next()) {
//                Optional<Seat> seatOpt = seatDAO.get(resultSet.getInt(1));
//                if (seatOpt.isPresent()) {
//                    seatList.add(seatOpt.get());
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//
//        }
//        return seatList;
        return null;
    }
}
