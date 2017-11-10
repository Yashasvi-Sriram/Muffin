package org.muffin.muffin.daoimplementations;


import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.ValidRegion;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.ValidRegionDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidRegionDAOImpl implements ValidRegionDAO {

    @Override
    public List<ValidRegion> search(String searchKey, int offset, int limit) {

        List<ValidRegion> validRegionsList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,city,state,country FROM valid_region WHERE city ILIKE ? or state ILIKE ? or country ILIKE ?  ORDER BY city OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            preparedStmt.setString(2, "%" + searchKey + "%");
            preparedStmt.setString(3, "%" + searchKey + "%");
            preparedStmt.setInt(4, offset);
            preparedStmt.setInt(5, limit);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                ValidRegion validRegion = new ValidRegion(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                validRegionsList.add(validRegion);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return validRegionsList;

    }

    @Override
    public Optional<ValidRegion> get(String city, String state, String country) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,city,state,country FROM valid_region WHERE city = ? and state = ? and country = ?")) {
            preparedStmt.setString(1, city);
            preparedStmt.setString(2, state);
            preparedStmt.setString(3, country);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                ValidRegion validRegion = new ValidRegion(result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
                return Optional.of(validRegion);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}

