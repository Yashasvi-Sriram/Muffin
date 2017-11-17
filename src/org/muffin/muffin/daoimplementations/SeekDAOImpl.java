package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Review;
import org.muffin.muffin.beans.Seek;
import org.muffin.muffin.daos.SeekDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<Seek> getById(int seekId) {
        String getSeeksQuery = "SELECT seek.id, muff.id, muff.handle, muff.name, muff.level, muff.joined_on, seek.text, seek.timestamp FROM muff, seek WHERE seek.id = ? AND muff.id = seek.muff_id";
        String getGenresQuery = "SELECT genre.id, genre.name FROM seek, seek_genre_r, genre WHERE seek.id = ? AND seek.id = seek_genre_r.seek_id AND seek_genre_r.genre_id = genre.id ORDER BY seek.timestamp DESC;";
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement getSeeksPS = conn.prepareStatement(getSeeksQuery);
             PreparedStatement getGenresPS = conn.prepareStatement(getGenresQuery);
        ) {
            getSeeksPS.setInt(1, seekId);
            ResultSet seekRS = getSeeksPS.executeQuery();
            if (seekRS.next()) {
                getGenresPS.setInt(1, seekId);
                ResultSet genresRS = getGenresPS.executeQuery();
                List<Genre> genres = new ArrayList<>();
                while (genresRS.next()) {
                    genres.add(new Genre(genresRS.getInt(1), genresRS.getString(2)));
                }
                return Optional.of(new Seek(
                        seekId,
                        new Muff(seekRS.getInt(2),
                                seekRS.getString(3),
                                seekRS.getString(4),
                                seekRS.getInt(5),
                                seekRS.getTimestamp(6).toLocalDateTime()
                        ),
                        seekRS.getString(7),
                        seekRS.getTimestamp(8).toLocalDateTime(),
                        genres
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Seek> getByMuff(int muffId, int offset, int limit, final Timestamp lastSeen) {
        String oldTuplesQuery = "SELECT seek.id, muff.id, muff.handle, muff.name, muff.level, muff.joined_on, seek.text, seek.timestamp FROM muff, seek WHERE muff.id = ? AND muff.id = seek.muff_id AND seek.timestamp <= ? ORDER BY seek.timestamp DESC OFFSET ? LIMIT ?;";
        String newTuplesQuery = "SELECT seek.id, muff.id, muff.handle, muff.name, muff.level, muff.joined_on, seek.text, seek.timestamp FROM muff, seek WHERE muff.id = ? AND muff.id = seek.muff_id AND seek.timestamp > ? ORDER BY seek.timestamp DESC;";
        String getGenresQuery = "SELECT genre.id, genre.name FROM seek, seek_genre_r, genre WHERE seek.id = ? AND seek.id = seek_genre_r.seek_id AND seek_genre_r.genre_id = genre.id;";
        return get(muffId, offset, limit, lastSeen, oldTuplesQuery, newTuplesQuery, getGenresQuery);
    }

    @Override
    public List<Seek> getByFollowers(int muffId, int offset, int limit, Timestamp lastSeen) {
        String oldTuplesQuery = "SELECT seek.id, muff.id, muff.handle, muff.name, muff.level, muff.joined_on, seek.text, seek.timestamp FROM muff, seek, follows WHERE follows.id1 = ? AND muff.id = follows.id2 AND muff.id = seek.muff_id AND seek.timestamp <= ? ORDER BY seek.timestamp DESC OFFSET ? LIMIT ?;";
        String newTuplesQuery = "SELECT seek.id, muff.id, muff.handle, muff.name, muff.level, muff.joined_on, seek.text, seek.timestamp FROM muff, seek, follows WHERE follows.id1 = ? AND muff.id = follows.id2 AND muff.id = seek.muff_id AND seek.timestamp > ? ORDER BY seek.timestamp DESC;";
        String getGenresQuery = "SELECT genre.id, genre.name FROM seek, seek_genre_r, genre WHERE seek.id = ? AND seek.id = seek_genre_r.seek_id AND seek_genre_r.genre_id = genre.id;";
        return get(muffId, offset, limit, lastSeen, oldTuplesQuery, newTuplesQuery, getGenresQuery);
    }

    private List<Seek> get(int id, int offset, int limit, Timestamp lastSeen, String oldTuplesQuery, String newTuplesQuery, String getGenresQuery) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement oldTuplesPS = conn.prepareStatement(oldTuplesQuery);
             PreparedStatement newTuplesPS = conn.prepareStatement(newTuplesQuery);
             PreparedStatement getGenresPS = conn.prepareStatement(getGenresQuery);
        ) {
            ResultSet seekRS;
            // new tuples are given priority
            newTuplesPS.setInt(1, id);
            newTuplesPS.setTimestamp(2, lastSeen);
            seekRS = newTuplesPS.executeQuery();
            List<Seek> seeks = resultSetConverter(seekRS, getGenresPS);

            // old tuples
            if (seeks.size() < limit) {
                oldTuplesPS.setInt(1, id);
                oldTuplesPS.setTimestamp(2, lastSeen);
                oldTuplesPS.setInt(3, offset);
                oldTuplesPS.setInt(4, limit - seeks.size());
                seekRS = oldTuplesPS.executeQuery();
                seeks.addAll(resultSetConverter(seekRS, getGenresPS));
            }
            return seeks;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Seek> resultSetConverter(ResultSet seeksRS, PreparedStatement getGenresPS) throws SQLException {
        List<Seek> seeks = new ArrayList<>();
        while (seeksRS.next()) {
            int seekId = seeksRS.getInt(1);
            getGenresPS.setInt(1, seekId);
            ResultSet genresRS = getGenresPS.executeQuery();
            List<Genre> genres = new ArrayList<>();
            while (genresRS.next()) {
                genres.add(new Genre(genresRS.getInt(1), genresRS.getString(2)));
            }
            seeks.add(new Seek(
                    seekId,
                    new Muff(seeksRS.getInt(2),
                            seeksRS.getString(3),
                            seeksRS.getString(4),
                            seeksRS.getInt(5),
                            seeksRS.getTimestamp(6).toLocalDateTime()
                    ),
                    seeksRS.getString(7),
                    seeksRS.getTimestamp(8).toLocalDateTime(),
                    genres
            ));
        }
        return seeks;
    }
}
