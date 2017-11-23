package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.ShowDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.Object;
import java.time.format.DateTimeFormatter;

public class ShowDAOImpl implements ShowDAO {
    private String toTSRange(Showtime showtime) {
        return "[" + showtime.getStartTime().toString() + "," + showtime.getEndTime().toString() + "]";
    }

    private String toTSUpperInfiniteRange(LocalDateTime currentTimeStamp) {
        return "[" + currentTimeStamp.toString() + "," + "" + "]";
    }

    @Override
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT theatre.id FROM theatre,cinema_building WHERE theatre.id = ? AND cinema_building_id = cinema_building.id AND owner_id = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO show(theatre_id,movie_id,during) VALUES (?,?,?::tsrange);")) {
            verifyStmt.setInt(1, theatreId);
            verifyStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()) {
                insertStmt.setInt(1, theatreId);
                insertStmt.setInt(2, movieId);
                insertStmt.setString(3, toTSRange(showtime));
                int result = insertStmt.executeUpdate();
                return result == 1;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Show> get(int movieId, int theatreId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during FROM show WHERE theatre_id = ?  AND movie_id = ? AND during = ?::tsrange")) {
            preparedStmt.setInt(1, theatreId);
            preparedStmt.setInt(2, movieId);
            preparedStmt.setString(3, toTSRange(showtime));
            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {
                StringTokenizer tokens = new StringTokenizer(resultSet.getString(4), ",");
                String initial = tokens.nextToken();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(initial.substring(2, initial.length() - 1), formatter);
                String helper = tokens.nextToken();
                helper = helper.substring(1, helper.length() - 2);
                LocalDateTime endTime = LocalDateTime.parse(helper, formatter);
                Showtime showtime1 = new Showtime(startTime, endTime);
                MovieDAO movieDAO = new MovieDAOImpl();
                Optional<Movie> movieOpt = movieDAO.get(resultSet.getInt(3));
                if (movieOpt.isPresent()) {
                    Show show = new Show(resultSet.getInt(1), resultSet.getInt(2), movieOpt.get(), showtime1);
                    return Optional.of(show);
                }
                return Optional.empty();
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public Optional<Show> getShow(int showId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during FROM show WHERE show.id = ?")) {
            preparedStmt.setInt(1, showId);

            ResultSet resultSet = preparedStmt.executeQuery();
            if (resultSet.next()) {

                StringTokenizer tokens = new StringTokenizer(resultSet.getString(4), ",");
                String initial = tokens.nextToken();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(initial.substring(2, initial.length() - 1), formatter);
                String helper = tokens.nextToken();
                helper = helper.substring(1, helper.length() - 2);
                LocalDateTime endTime = LocalDateTime.parse(helper, formatter);
                MovieDAO movieDAO = new MovieDAOImpl();
                Showtime showtime1 = new Showtime(startTime, endTime);
                Optional<Movie> movieOpt = movieDAO.get(resultSet.getInt(3));
                if (movieOpt.isPresent()) {
                    Show show = new Show(resultSet.getInt(1), resultSet.getInt(2), movieOpt.get(), showtime1);
                    return Optional.of(show);
                }
                return Optional.empty();
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public List<Show> get(int theatreId) {
        List<Show> showList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id,theatre_id,movie_id,during FROM show WHERE show.theatre_id = ?")) {
            preparedStmt.setInt(1, theatreId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                StringTokenizer tokens = new StringTokenizer(result.getString(4), ",");
                String initial = tokens.nextToken();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(initial.substring(2, initial.length() - 1), formatter);
                String helper = tokens.nextToken();
                helper = helper.substring(1, helper.length() - 2);
                LocalDateTime endTime = LocalDateTime.parse(helper, formatter);
                Showtime showtime1 = new Showtime(startTime, endTime);
                MovieDAO movieDAO = new MovieDAOImpl();
                Optional<Movie> movieOpt = movieDAO.get(result.getInt(3));
                if (movieOpt.isPresent()) {
                    Show show = new Show(result.getInt(1), result.getInt(2), movieOpt.get(), showtime1);
                    showList.add(show);
                }

            }
            return showList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean delete(int showId, int cinemaBuildingOwnerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT show.id FROM show,theatre,cinema_building WHERE show.id = ? AND show.theatre_id = theatre.id AND cinema_building_id = cinema_building.id AND owner_id = ?");
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM show WHERE id = ?")) {
            verifyStmt.setInt(1, showId);
            verifyStmt.setInt(2, cinemaBuildingOwnerId);
            ResultSet resultSet = verifyStmt.executeQuery();
            if (resultSet.next()) {
                deleteStmt.setInt(1, showId);
                int result = deleteStmt.executeUpdate();
                return result == 1;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public List<Movie> getActiveMovies(String pattern, int limit, int offset, LocalDateTime currentTimeStamp) {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT movie.id, movie.owner_id, movie.name, movie.duration FROM movie WHERE movie.name ILIKE ? AND movie.id in (SELECT show.movie_id from show WHERE (show.during && ?::tsrange) ) ORDER BY movie.name OFFSET ? LIMIT ?")) {
            preparedStmt.setString(1, "%" + pattern + "%");
            preparedStmt.setString(2, toTSUpperInfiniteRange(currentTimeStamp));
            preparedStmt.setInt(3, offset);
            preparedStmt.setInt(4, limit);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                int movieId = rs.getInt(1);
                GenreDAO genreDAO = new GenreDAOImpl();
                List<Genre> genres = genreDAO.getByMovie(movieId);
                Movie movie = new Movie(movieId, rs.getInt(2), rs.getString(3), rs.getInt(4), genres);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public Map<CinemaBuilding, List<Show>> getAllShows(int movieId, String city, String state, String country, LocalDateTime currentTimeStamp) {

        Map<CinemaBuilding, List<Show>> shows = new HashMap<>();
        CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT cinema_building.id,show.id from cinema_building,theatre,show where show.theatre_id = theatre.id AND theatre.cinema_building_id = cinema_building.id AND show.movie_id = ? AND cinema_building.city =  ? AND cinema_building.state = ? AND cinema_building.country = ? AND (show.during && ?::tsrange)")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setString(2, city);
            preparedStmt.setString(3, state);
            preparedStmt.setString(4, country);
            preparedStmt.setString(5, toTSUpperInfiniteRange(currentTimeStamp));
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {
                CinemaBuilding cinemaBuilding = cinemaBuildingDAO.get(resultSet.getInt(1)).get();
                Show show = getShow(resultSet.getInt(2)).get();

                if (shows.containsKey(cinemaBuilding)) {
                    shows.get(cinemaBuilding).add(show);

                } else {
                    List<Show> showList = new ArrayList<>();
                    showList.add(show);
                    shows.put(cinemaBuilding, showList);
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shows;

    }
}
