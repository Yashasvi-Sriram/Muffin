package org.muffin.muffin.daoimplementations;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.CinemaBuildingDAO;

import org.muffin.muffin.db.DBConfig;


public class CinemaBuildingDAOImpl implements CinemaBuildingDAO {

    @Override
    public boolean create(int ownerId, String name, String streetName, String city, String state, String country,
                          String zip) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO cinema_building(owner_id,name,street_name,city,state,country,zip) VALUES (?,?,?,?,?,?,?);")) {
            preparedStmt.setInt(1, ownerId);
            preparedStmt.setString(2, name);
            preparedStmt.setString(3, streetName);
            preparedStmt.setString(4, city);
            preparedStmt.setString(5, state);
            preparedStmt.setString(6, country);
            preparedStmt.setString(7, zip);

            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<CinemaBuilding> get(String name, String streetName, String city, String state, String country,
                                        String zip) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, owner_id, name, street_name, city, state, country, zip  FROM cinema_building WHERE name = ? and street_name = ? and city = ? and state = ? and country = ? and zip = ? ")) {
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, streetName);
            preparedStmt.setString(3, city);
            preparedStmt.setString(4, state);
            preparedStmt.setString(5, country);
            preparedStmt.setString(6, zip);

            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {

                CinemaBuilding cinemaBuilding = new CinemaBuilding(result.getInt(1), result.getInt(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7), result.getString(8));
                return Optional.of(cinemaBuilding);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<CinemaBuilding> getByOwner(int ownerId) {
        List<CinemaBuilding> cinemaBuildingsList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM cinema_building WHERE cinema_building.owner_id = ?")) {
            preparedStmt.setInt(1, ownerId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {

                CinemaBuilding cinemaBuilding = new CinemaBuilding(result.getInt(1), result.getInt(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7), result.getString(8));
                cinemaBuildingsList.add(cinemaBuilding);
            }
            return cinemaBuildingsList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean delete(int cinemaBuildingId, int ownerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM cinema_building WHERE id = ? AND owner_id = ?;")) {
            preparedStmt.setInt(1, cinemaBuildingId);
            preparedStmt.setInt(2, ownerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

