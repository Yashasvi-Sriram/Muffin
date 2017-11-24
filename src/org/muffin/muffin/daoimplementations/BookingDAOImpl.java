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
        PreparedStatement insertBookedShowSeats = null;
        PreparedStatement insertBooking = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
            insertBooking = conn.prepareStatement("INSERT INTO booking(show_id,muff_id) VALUES (?,?) RETURNING id");
            insertBookedShowSeats = conn.prepareStatement("INSERT INTO booked_show_seats(theatre_id,seat_id,show_id,booking_id) VALUES(?,?,?,?)");
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            insertBooking.setInt(1, showId);
            insertBooking.setInt(2, muffId);
            ResultSet rs = insertBooking.executeQuery();
            if (rs.next()) {
                int bookingId = rs.getInt(1);
                for (Seat seat : bookedShowSeats) {
                    insertBookedShowSeats.setInt(1, seat.getTheatreId());
                    insertBookedShowSeats.setInt(2, seat.getId());
                    insertBookedShowSeats.setInt(3, showId);
                    insertBookedShowSeats.setInt(4, bookingId);
                    insertBookedShowSeats.executeUpdate();
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.print("Book is being rolled back");
                    conn.rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            try {
                if (insertBooking != null) {
                    insertBooking.close();
                }
                if (insertBookedShowSeats != null) {
                    insertBookedShowSeats.close();
                }
                if (conn != null) {
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<Seat> getBookedSeats(final int showId) {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT s.id, s.theatre_id, s.x, s.y FROM booked_show_seats AS bss, seat AS s WHERE bss.seat_id = s.id AND bss.show_id = ?;")) {
            preparedStmt.setInt(1, showId);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    @Override
    public List<Booking> getMuffBookingHistory(int muffId) {

        List<Booking> bookingList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT booking.id,show_id,muff_id, movie.name, booked_on FROM booking, movie, show WHERE booking.show_id = show.id AND  show.movie_id = movie.id AND muff_id = ? ");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT seat.id,seat.theatre_id,x,y FROM booked_show_seats, seat WHERE booked_show_seats.seat_id = seat.id AND booked_show_seats.booking_id = ? ")) {
            preparedStmt.setInt(1, muffId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                preparedStmt2.setInt(1, resultSet.getInt(1));
                ResultSet resultSet2 = preparedStmt2.executeQuery();
                List<Seat> seatList = new ArrayList<>();
                while (resultSet2.next()) {
                    Seat seat = new Seat(resultSet2.getInt(1), resultSet2.getInt(2), resultSet2.getInt(3), resultSet2.getInt(4));
                    seatList.add(seat);
                }
                Booking booking = new Booking(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3), resultSet.getString(4), resultSet.getTimestamp(5).toLocalDateTime(), seatList);
                bookingList.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return bookingList;


    }
}
