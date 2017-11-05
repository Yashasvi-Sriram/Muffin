package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.CinemaBuildingOwnerDAO;

import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.Optional;


public class CinemaBuildingOwnerDAOImpl implements  CinemaBuildingOwnerDAO{

	@Override
	public boolean create(String handle, String name, String password) {
		try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
	             PreparedStatement createCBO = conn.prepareStatement("INSERT INTO cinema_building_owner(handle, name) VALUES (?, ?);");
	             PreparedStatement getCBOId = conn.prepareStatement("SELECT id FROM cinema_building_owner WHERE handle = ?;");
	             PreparedStatement createCBOPassword = conn.prepareStatement("INSERT INTO cinema_building_owner_password(id, password) VALUES (?, ?);");) {
	            // createMuff
	            createCBO.setString(1, handle);
	            createCBO.setString(2, name);
	            int result = createCBO.executeUpdate();
	            if (result != 1) {
	                return false;
	            }
	            // getCreatedMuff
	            getCBOId.setString(1, handle);
	            ResultSet rs = getCBOId.executeQuery();
	            if (rs.next()) {
	                int id = rs.getInt(1);
	                createCBOPassword.setInt(1, id);
	                createCBOPassword.setString(2, password);
	                result = createCBOPassword.executeUpdate();
	                return result == 1;
	            } else {
	                return false;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
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
