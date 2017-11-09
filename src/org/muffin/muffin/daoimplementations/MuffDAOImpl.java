package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<Muff> search(String searchKey, final int offset, final int limit) {
        List<Muff> muffs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, level, joined_on FROM muff WHERE name ILIKE ? OR handle ILIKE ? ORDER BY handle OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + searchKey + "%");
            preparedStmt.setString(2, "%" + searchKey + "%");
            preparedStmt.setInt(3, offset);
            preparedStmt.setInt(4, limit);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Muff muff = new Muff(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5).toLocalDateTime());
                muffs.add(muff);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return muffs;
    }

    @Override
    public List<Muff> getFollowees(int muffId) {
        List<Muff> muffs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, level, joined_on FROM follows,muff WHERE follows.id1 = ?  AND follows.id2 = muff.id")) {
            preparedStmt.setInt(1, muffId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Muff muff = new Muff(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5).toLocalDateTime());
                muffs.add(muff);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return muffs;
    }

    @Override
    public List<Muff> getFollowers(int muffId) {
        List<Muff> muffs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, handle, name, level, joined_on FROM follows,muff WHERE follows.id1 = muff.id  AND follows.id2 = ?")) {
            preparedStmt.setInt(1, muffId);
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                Muff muff = new Muff(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5).toLocalDateTime());
                muffs.add(muff);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return muffs;
    }

    @Override
    public Optional<Boolean> toggleFollow(int muffId, int followeeId) {
        // One db connection opens and closes here
        Optional<Boolean> doesFollow = doesFollow(muffId, followeeId);
        if (doesFollow.isPresent()) {
            try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
                 PreparedStatement insert = conn.prepareStatement("INSERT INTO follows(id1, id2) VALUES (?,?)");
                 PreparedStatement delete = conn.prepareStatement("DELETE FROM follows WHERE (id1, id2) = (?,?)")) {
                if (doesFollow.get()) {
                    insert.setInt(1, muffId);
                    insert.setInt(2, followeeId);
                    int result = insert.executeUpdate();
                    return result == 1 ? Optional.of(true) : Optional.empty();
                } else {
                    delete.setInt(1, muffId);
                    delete.setInt(2, followeeId);
                    int result = delete.executeUpdate();
                    return result == 1 ? Optional.of(false) : Optional.empty();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> doesFollow(int muffId, int followeeId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT count(*) FROM follows WHERE follows.id1 = ? AND follows.id2 = ?")) {
            preparedStmt.setInt(1, muffId);
            preparedStmt.setInt(2, followeeId);
            ResultSet resultSet = preparedStmt.executeQuery();
            return Optional.of(resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
