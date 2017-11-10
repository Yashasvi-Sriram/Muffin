package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Theatre;


import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class TheatreDAOImpl implements TheatreDAO {

    @Override
    public List<Theatre> getByCinemaBuilding(int cinemaBuildingId, int cinemaBuildingOwnerId) {

        List<Theatre> theatresList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT theatre.id, theatre.cinema_building_id, theatre.screen_no FROM theatre WHERE theatre.cinema_building_id = ? AND theatre.cinema_building_id in (SELECT  cinema_building.id FROM  cinema_building WHERE cinema_building.owner_id = ?); ")) {
            preparedStmt.setInt(1, cinemaBuildingId);
            preparedStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Theatre theatre = new Theatre(
                        result.getInt(1),
                        result.getInt(2),
                        result.getInt(3)
                );
                theatresList.add(theatre);
            }
            return theatresList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }


    }

    @Override
    public Optional<Theatre> get(int cinemaBuildingId, int screenNo) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,cinema_building_id,screen_no FROM theatre WHERE cinema_building_id = ? and screen_no = ?");) {

            preparedStmt.setInt(1, cinemaBuildingId);
            preparedStmt.setInt(2, screenNo);


            ResultSet result = preparedStmt.executeQuery();

            if (result.next()) {

                Theatre theatre = new Theatre(result.getInt(1), result.getInt(2), result.getInt(3));
                return Optional.of(theatre);

            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public boolean create(int cinemaBuildingId, int screenNo, int cinemaBuildingOwnerId) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO theatre(cinema_building_id,screen_no) SELECT id,? FROM cinema_building WHERE id = ? AND owner_id = ? ;")) {

            preparedStmt.setInt(2, cinemaBuildingId);
            preparedStmt.setInt(1, screenNo);
            preparedStmt.setInt(3, cinemaBuildingOwnerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean delete(int theatreId, int cinemaBuildingOwnerId) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM theatre WHERE id = ? AND cinema_building_id in (SELECT  cinema_building.id FROM  cinema_building WHERE cinema_building.owner_id = ?);")) {
            preparedStmt.setInt(1, theatreId);
            preparedStmt.setInt(2, cinemaBuildingOwnerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}

