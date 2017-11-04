package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorDAOImpl implements ActorDAO {
    @Override
    public List<Actor> search(String searchKey) {
        List<Actor> actorList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM actor WHERE name ilike ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Actor actor = new Actor(resultSet.getInt(1), resultSet.getString(2));
                actorList.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return actorList;
    }

	@Override
	public boolean create(String name) {
		try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
	             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO actor(name) VALUES (?);")) {
	            preparedStmt.setString(1, name);
	          
	            int result = preparedStmt.executeUpdate();
	            return result == 1;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	}

	@Override
	public Optional<Actor> get(String name) {
		// TODO Auto-generated method stub
		try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
	             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, name FROM actor WHERE name = ?")) {
	            preparedStmt.setString(1, name);
	            ResultSet result = preparedStmt.executeQuery();
	            if (result.next()) {
	                Actor actor = new Actor(result.getInt(1), result.getString(2));
	                return Optional.of(actor);
	            }
	            return Optional.empty();
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return Optional.empty();
	        }
	}
}
