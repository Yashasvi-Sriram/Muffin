package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.daos.SeekDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.List;

public class SeekDAOImpl implements SeekDAO {
    @Override
    public boolean create(int muffId, String text, List<Integer> genreIds) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement createSeek = conn.prepareStatement("INSERT INTO seek(muff_id, text, timestamp) VALUES (?, ?, CURRENT_TIMESTAMP) RETURNING id;");
             PreparedStatement createSeekGeneres = conn.prepareStatement("INSERT INTO seek_genre_r(seek_id, genre_id) VALUES (?, ?);");) {
            // create seek
            createSeek.setInt(1, muffId);
            createSeek.setString(2, text);
            ResultSet resultSet = createSeek.executeQuery();
            if (!resultSet.next()) {
                return false;
            }
            int seekId = resultSet.getInt(1);
            for (Integer genreId : genreIds) {
                // create seek genre map
                createSeekGeneres.setInt(1, seekId);
                createSeekGeneres.setInt(2, genreId);
                if (createSeekGeneres.executeUpdate() != 1) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int seekId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement deleteSeek = conn.prepareStatement("DELETE FROM seek WHERE id = ?;")) {
            deleteSeek.setInt(1, seekId);
            return deleteSeek.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
