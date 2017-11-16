package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.CinemaBuildingOwnerDAO;

import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.Optional;


public class CinemaBuildingOwnerDAOImpl implements CinemaBuildingOwnerDAO {

    @Override
    public Optional<CinemaBuildingOwner> create(String handle, String name, String password) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement createCBO = conn.prepareStatement("INSERT INTO cinema_building_owner(handle, name) VALUES (?, ?) RETURNING id, handle, name, joined_on;");
             PreparedStatement createCBOPassword = conn.prepareStatement("INSERT INTO cinema_building_owner_password(id, password) VALUES (?, ?);");) {
            // createCBO
            createCBO.setString(1, handle);
            createCBO.setString(2, name);
            ResultSet rs = createCBO.executeQuery();
            if (rs.next()) {
                CinemaBuildingOwner cinemaBuildingOwner = new CinemaBuildingOwner(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime());
                // createCBO password
                createCBOPassword.setInt(1, cinemaBuildingOwner.getId());
                createCBOPassword.setString(2, password);
                if (createCBOPassword.executeUpdate() == 1) {

                    return Optional.of(cinemaBuildingOwner);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(String handle, String password) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT count(*) FROM cinema_building_owner, cinema_building_owner_password WHERE cinema_building_owner.id = cinema_building_owner_password.id AND handle=? AND password=?;")) {
            preparedStmt.setString(1, handle);
            preparedStmt.setString(2, password);
            ResultSet result = preparedStmt.executeQuery();
            result.next();
            return (result.getInt(1) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<CinemaBuildingOwner> get(String handle) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, joined_on FROM cinema_building_owner WHERE handle = ?")) {
            preparedStmt.setString(1, handle);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                CinemaBuildingOwner cinemaBuildingOwner = new CinemaBuildingOwner(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getTimestamp(4).toLocalDateTime());
                return Optional.of(cinemaBuildingOwner);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
