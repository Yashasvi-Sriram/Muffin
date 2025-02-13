package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.*;
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
             PreparedStatement createSeek = conn.prepareStatement("INSERT INTO seek_response(muff_id, seek_id, movie_id, text, timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING id, seek_id, text, approval_status, timestamp;");
             PreparedStatement getMuff = conn.prepareStatement("SELECT id, handle, name, no_approvals, joined_on  FROM muff WHERE id = ?");
             PreparedStatement getMovie = conn.prepareStatement("SELECT name FROM movie WHERE id = ?");
        ) {
            // create seek response
            createSeek.setInt(1, muffId);
            createSeek.setInt(2, seekId);
            createSeek.setInt(3, movieId);
            createSeek.setString(4, text);
            ResultSet rs = createSeek.executeQuery();
            if (rs.next()) {
                getMuff.setInt(1, muffId);
                ResultSet muffRS = getMuff.executeQuery();
                getMovie.setInt(1, movieId);
                ResultSet movieRS = getMovie.executeQuery();
                if (muffRS.next() && movieRS.next()) {
                    return Optional.of(new SeekResponse(
                            rs.getInt(1),
                            new Muff(
                                    muffRS.getInt(1),
                                    muffRS.getString(2),
                                    muffRS.getString(3),
                                    muffRS.getInt(4),
                                    muffRS.getTimestamp(5).toLocalDateTime()
                            ),
                            rs.getInt(2),
                            movieId,
                            movieRS.getString(1),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getTimestamp(5).toLocalDateTime()
                    ));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<SeekResponse> getBySeek(int seekId, int offset, int limit, final Timestamp lastSeen) {
        String oldTuplesQuery = "SELECT sr.id, muff.id, muff.handle, muff.name, muff.no_approvals, muff.joined_on, sr.seek_id, movie.id, movie.name, sr.text, sr.approval_status, sr.timestamp FROM seek_response AS sr, muff, movie WHERE sr.seek_id = ? AND sr.muff_id = muff.id AND sr.movie_id = movie.id AND sr.timestamp <= ? ORDER BY sr.timestamp DESC OFFSET ? LIMIT ?;";
        String newTuplesQuery = "SELECT sr.id, muff.id, muff.handle, muff.name, muff.no_approvals, muff.joined_on, sr.seek_id, movie.id, movie.name, sr.text, sr.approval_status, sr.timestamp FROM seek_response AS sr, muff, movie WHERE sr.seek_id = ? AND sr.muff_id = muff.id AND sr.movie_id = movie.id AND sr.timestamp > ? ORDER BY sr.timestamp DESC;";
        return get(seekId, offset, limit, lastSeen, oldTuplesQuery, newTuplesQuery);
    }

    @Override
    public List<Integer> getMuffIdsOfAllSeekResponsesOfSeek(int seekId) {
        List<Integer> muffIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement getMuffIds = conn.prepareStatement("SELECT muff_id FROM seek_response WHERE seek_id = ?")) {
            getMuffIds.setInt(1, seekId);
            ResultSet rs = getMuffIds.executeQuery();
            while (rs.next()) {
                muffIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return muffIds;
    }

    @Override
    public Optional<Boolean> checkForNewResponsesOfSeek(int seekId, Timestamp lastSeen) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement checkForNew = conn.prepareStatement("SELECT count(*) FROM seek_response WHERE seek_id = ? AND timestamp > ?;");
        ) {
            checkForNew.setInt(1, seekId);
            checkForNew.setTimestamp(2, lastSeen);
            ResultSet rs = checkForNew.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt(1) > 0);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> toggleApproval(int seekResponseId) {
        // One db connection opens and closes here
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement toggleApproval = conn.prepareStatement("UPDATE seek_response SET approval_status = CASE (SELECT approval_status FROM seek_response WHERE id = ?) WHEN 0 THEN 1 ELSE 0 END, timestamp = CURRENT_TIMESTAMP WHERE id = ? RETURNING approval_status;");) {
            toggleApproval.setInt(1, seekResponseId);
            toggleApproval.setInt(2, seekResponseId);
            ResultSet rs = toggleApproval.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getInt(1));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<SeekResponse> getById(int seekResponseId) {
        String getSeeksQuery = "SELECT sr.id, muff.id, muff.handle, muff.name, muff.no_approvals, muff.joined_on, sr.seek_id, movie.id, movie.name, sr.text, sr.approval_status, sr.timestamp FROM seek_response AS sr, muff, movie WHERE sr.id = ? AND sr.muff_id = muff.id AND sr.movie_id = movie.id;";
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement getSeeksPS = conn.prepareStatement(getSeeksQuery);
        ) {
            getSeeksPS.setInt(1, seekResponseId);
            ResultSet rs = getSeeksPS.executeQuery();
            if (rs.next()) {
                return Optional.of(new SeekResponse(
                        rs.getInt(1),
                        new Muff(
                                rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getInt(5),
                                rs.getTimestamp(6).toLocalDateTime()
                        ),
                        rs.getInt(7),
                        rs.getInt(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getInt(11),
                        rs.getTimestamp(12).toLocalDateTime()
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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

    private List<SeekResponse> resultSetConverter(ResultSet rs) throws SQLException {
        List<SeekResponse> seeks = new ArrayList<>();
        while (rs.next()) {
            seeks.add(new SeekResponse(
                    rs.getInt(1),
                    new Muff(
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5),
                            rs.getTimestamp(6).toLocalDateTime()
                    ),
                    rs.getInt(7),
                    rs.getInt(8),
                    rs.getString(9),
                    rs.getString(10),
                    rs.getInt(11),
                    rs.getTimestamp(12).toLocalDateTime()
            ));
        }
        return seeks;
    }
}
