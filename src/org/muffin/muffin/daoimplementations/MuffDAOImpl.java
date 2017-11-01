package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.Optional;

public class MuffDAOImpl implements MuffDAO {

    @Override
    public boolean create(String handle, String name, String password) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement createMuff = conn.prepareStatement("INSERT INTO muff(handle, name) VALUES (?, ?);");
             PreparedStatement getMuffId = conn.prepareStatement("SELECT id FROM muff WHERE handle = ?;");
             PreparedStatement createMuffPassword = conn.prepareStatement("INSERT INTO muff_password(id, password) VALUES (?, ?);");) {
            // createMuff
            createMuff.setString(1, handle);
            createMuff.setString(2, name);
            int result = createMuff.executeUpdate();
            if (result != 1) {
                return false;
            }
            // getCreatedMuff
            getMuffId.setString(1, handle);
            ResultSet rs = getMuffId.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                createMuffPassword.setInt(1, id);
                createMuffPassword.setString(2, password);
                result = createMuffPassword.executeUpdate();
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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT count(*) FROM muff, muff_password WHERE muff.id = muff_password.id AND handle=? AND password=?;")) {
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
    public Optional<Muff> get(String handle) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, level, joined_on FROM muff WHERE handle = ?")) {
            preparedStmt.setString(1, handle);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Muff muff = new Muff(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getTimestamp(5).toLocalDateTime());
                return Optional.of(muff);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Muff> get(int id) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, level, joined_on FROM muff WHERE id = ?")) {
            preparedStmt.setInt(1, id);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                Muff muff = new Muff(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getTimestamp(5).toLocalDateTime());
                return Optional.of(muff);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
