package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.Optional;

public class ActorDAOImpl implements ActorDAO {
    @Override
    public Optional<Actor> search(String searchkey) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM actor WHERE name ilike ?")) {
            preparedStmt.setString(1,"%" + searchkey + "%");
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()){
                Actor actor = new Actor(resultSet.getInt(1),resultSet.getString(2));
                return Optional.of(actor);
            }
            else{
                return Optional.empty();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }

    }
}
