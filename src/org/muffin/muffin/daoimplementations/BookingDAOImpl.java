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

    @Override
    public List<Booking> getMuffBookingHistory(int muffId) {

        List<Booking> bookingList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,show_id,muff_id,booked_on from booking where muff_id = ? ");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT seat.id,seat.theatre_id,x,y from booked_show_seats.seat WHERE booked_show_seats.seat_id = seat.id AND booked_show_seats.booking_id = ? ")) {
            preparedStmt.setInt(1, muffId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                preparedStmt2.setInt(1,resultSet.getInt(1));
                ResultSet resultSet2 = preparedStmt2.executeQuery();
                List<Seat> seatList = new ArrayList<>();
                while (resultSet.next()) {
                    Seat seat = new Seat(resultSet2.getInt(1),resultSet2.getInt(2),resultSet2.getInt(3),resultSet2.getInt(4));
                    seatList.add(seat);

                }

                Booking booking = new Booking(resultSet.getInt(1),resultSet.getInt(2),resultSet.getInt(3),resultSet.getTimestamp(4).toLocalDateTime(),seatList);
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return bookingList;


    }
}
