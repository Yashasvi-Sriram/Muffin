package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
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
}
