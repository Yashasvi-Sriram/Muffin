package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seek;
import org.muffin.muffin.beans.SeekResponse;
import org.muffin.muffin.daos.SeekDAO;
import org.muffin.muffin.daos.SeekResponseDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeekResponseDAOImpl implements SeekResponseDAO {
    @Override
    public Optional<SeekResponse> create(final int muffId, final int seekId, final int movieId, final String text) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement createSeek = conn.prepareStatement("INSERT INTO seek_response(muff_id, seek_id, movie_id, text, timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING id, muff_id, seek_id, movie_id, text, timestamp;");) {
            // create seek response
            createSeek.setInt(1, muffId);
            createSeek.setInt(2, seekId);
            createSeek.setInt(3, movieId);
            createSeek.setString(4, text);
            ResultSet rs = createSeek.executeQuery();
            if (rs.next()) {
                return Optional.of(new SeekResponse(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getTimestamp(6).toLocalDateTime()
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<SeekResponse> getBySeek(int seekId, int offset, int limit, final Timestamp lastSeen) {
        String oldTuplesQuery = "SELECT id, muff_id, seek_id, movie_id, text, timestamp FROM seek_response WHERE seek_id = ? AND timestamp <= ? ORDER BY timestamp DESC OFFSET ? LIMIT ?;";
        String newTuplesQuery = "SELECT id, muff_id, seek_id, movie_id, text, timestamp FROM seek_response WHERE seek_id = ? AND timestamp > ? ORDER BY timestamp DESC;";
        return get(seekId, offset, limit, lastSeen, oldTuplesQuery, newTuplesQuery);
    }

    private List<SeekResponse> get(int id, int offset, int limit, Timestamp lastSeen, String oldTuplesQuery, String newTuplesQuery) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement oldTuplesPS = conn.prepareStatement(oldTuplesQuery);
             PreparedStatement newTuplesPS = conn.prepareStatement(newTuplesQuery);
        ) {
            ResultSet seekResponseRS;
            // new tuples are given priority
            newTuplesPS.setInt(1, id);
            newTuplesPS.setTimestamp(2, lastSeen);
            seekResponseRS = newTuplesPS.executeQuery();
            List<SeekResponse> seekResponses = resultSetConverter(seekResponseRS);

            // old tuples
            if (seekResponses.size() < limit) {
                oldTuplesPS.setInt(1, id);
                oldTuplesPS.setTimestamp(2, lastSeen);
                oldTuplesPS.setInt(3, offset);
                oldTuplesPS.setInt(4, limit - seekResponses.size());
                seekResponseRS = oldTuplesPS.executeQuery();
                seekResponses.addAll(resultSetConverter(seekResponseRS));
            }
            return seekResponses;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<SeekResponse> resultSetConverter(ResultSet seekResponseRS) throws SQLException {
        List<SeekResponse> seeks = new ArrayList<>();
        while (seekResponseRS.next()) {
            seeks.add(new SeekResponse(
                    seekResponseRS.getInt(1),
                    seekResponseRS.getInt(2),
                    seekResponseRS.getInt(3),
                    seekResponseRS.getInt(4),
                    seekResponseRS.getString(5),
                    seekResponseRS.getTimestamp(6).toLocalDateTime()
            ));
        }
        return seeks;
    }
}
