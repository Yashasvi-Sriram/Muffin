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
    @Override
    public boolean create(int movieId, int theatreId, int cinemaBuildingOwnerId, Showtime showtime) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement verifyStmt = conn.prepareStatement("SELECT theatre.id FROM theatre,cinema_building WHERE theatre.id = ? AND cinema_building_id = cinema_building.id AND owner_id = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO show(theatre_id,movie_id,during) VALUES (?,?,?::TSRANGE);")) {
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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT show.id,theatre_id,show.movie_id,during,movie.name,movie.owner_id,movie.duration FROM show,movie WHERE theatre_id = ?  AND show.movie_id = ? AND show.movie_id = movie.id AND during = ?::TSRANGE");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT genre.id, genre.name FROM genre, movie_genre_r WHERE movie_id = ? AND genre.id = genre_id")) {
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
                List<Genre> genres = new ArrayList<>();
                preparedStmt2.setInt(1, resultSet.getInt(3));
                ResultSet resultSet2 = preparedStmt2.executeQuery();
                while (resultSet2.next()) {
                    Genre genre = new Genre(resultSet2.getInt(1), resultSet2.getString(2));
                    genres.add(genre);
                }
                Movie movie = new Movie(resultSet.getInt(3), resultSet.getInt(6), resultSet.getString(5), resultSet.getInt(7), genres);
                Show show = new Show(resultSet.getInt(1), resultSet.getInt(2), movie, showtime1);
                return Optional.of(show);

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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT show.id,theatre_id,show.movie_id,during,movie.name,movie.owner_id,movie.duration FROM show,movie WHERE show.theatre_id = ?  AND show.movie_id = movie.id ORDER BY during");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT genre.id, genre.name FROM genre, movie_genre_r WHERE movie_id = ? AND genre.id = genre_id")) {
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

                preparedStmt2.setInt(1, result.getInt(3));
                List<Genre> genres = new ArrayList<>();
                ResultSet resultSet2 = preparedStmt2.executeQuery();
                while (resultSet2.next()) {
                    Genre genre = new Genre(resultSet2.getInt(1), resultSet2.getString(2));
                    genres.add(genre);
                }
                Movie movie = new Movie(result.getInt(3), result.getInt(6), result.getString(5), result.getInt(7), genres);
                Show show = new Show(result.getInt(1), result.getInt(2), movie, showtime1);
                showList.add(show);


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
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT movie.id, movie.owner_id, movie.name, movie.duration FROM movie WHERE movie.name ILIKE ? AND movie.id IN (SELECT show.movie_id FROM show WHERE (show.during && ?::TSRANGE) ) ORDER BY movie.name OFFSET ? LIMIT ?");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT genre.id, genre.name FROM genre, movie_genre_r WHERE movie_id = ? AND genre.id = genre_id")) {
            preparedStmt.setString(1, "%" + pattern + "%");
            preparedStmt.setString(2, toTSUpperInfiniteRange(currentTimeStamp));
            preparedStmt.setInt(3, offset);
            preparedStmt.setInt(4, limit);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                int movieId = rs.getInt(1);
                preparedStmt2.setInt(1, movieId);
                List<Genre> genres = new ArrayList<>();
                ResultSet resultSet2 = preparedStmt2.executeQuery();
                while (resultSet2.next()) {
                    Genre genre = new Genre(resultSet2.getInt(1), resultSet2.getString(2));
                    genres.add(genre);
                }
                Movie movie = new Movie(movieId, rs.getInt(2), rs.getString(3), rs.getInt(4), genres);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public Map<CinemaBuilding, List<Show>> getAllShows(int movieId, String city, String state, String country, Showtime showtime) {

        Map<CinemaBuilding, List<Show>> shows = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT cinema_building.id,show.id FROM cinema_building,theatre,show WHERE show.theatre_id = theatre.id AND theatre.cinema_building_id = cinema_building.id AND show.movie_id = ? AND cinema_building.city =  ? AND cinema_building.state = ? AND cinema_building.country = ? AND (show.during && ?::TSRANGE) ORDER BY during");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT id, owner_id, name, street_name, city, state, country, zip  FROM cinema_building WHERE id = ?")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setString(2, city);
            preparedStmt.setString(3, state);
            preparedStmt.setString(4, country);
            preparedStmt.setString(5, toTSRange(showtime));
            ResultSet resultSet = preparedStmt.executeQuery();
            while (resultSet.next()) {

                preparedStmt2.setInt(1, resultSet.getInt(1));
                ResultSet result = preparedStmt.executeQuery();

                if (result.next()) {
                    CinemaBuilding cinemaBuilding = new CinemaBuilding(result.getInt(1), result.getInt(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7), result.getString(8));
                    Show show = getShow(resultSet.getInt(2), conn).get();

                    if (shows.containsKey(cinemaBuilding)) {
                        shows.get(cinemaBuilding).add(show);

                    } else {
                        List<Show> showList = new ArrayList<>();
                        showList.add(show);
                        shows.put(cinemaBuilding, showList);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shows;

    }

    private String toTSRange(Showtime showtime) {
        return "[" + showtime.getStartTime().toString() + "," + showtime.getEndTime().toString() + "]";
    }

    private String toTSUpperInfiniteRange(LocalDateTime currentTimeStamp) {
        return "[" + currentTimeStamp.toString() + "," + "" + "]";
    }

    private Optional<Show> getShow(int showId, Connection conn) throws SQLException {

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT show.id,theatre_id,show.movie_id,during,movie.name,movie.owner_id,movie.duration FROM show,movie WHERE show.id = ? AND show.movie_id = movie.id");
        PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT genre.id, genre.name FROM genre, movie_genre_r WHERE movie_id = ? AND genre.id = genre_id");
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
            Showtime showtime1 = new Showtime(startTime, endTime);

            preparedStmt2.setInt(1, resultSet.getInt(2));
            List<Genre> genres = new ArrayList<>();
            ResultSet resultSet2 = preparedStmt2.executeQuery();
            while (resultSet2.next()) {
                Genre genre = new Genre(resultSet2.getInt(1), resultSet2.getString(2));
                genres.add(genre);
            }
            Movie movie = new Movie(resultSet.getInt(3), resultSet.getInt(6), resultSet.getString(5), resultSet.getInt(7), genres);
            Show show = new Show(resultSet.getInt(1), resultSet.getInt(2), movie, showtime1);
            return Optional.of(show);


        }
        return Optional.empty();


    }
}
