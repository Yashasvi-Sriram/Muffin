package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.CharacterDAO;

import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class CharacterDAOImpl implements CharacterDAO {
    @Override
    public List<Character> getByMovie(int movieId, int movieOwnerId) {
        List<Character> characterList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT character.id, character.name, character.movie_id, actor.id, actor.name FROM character, actor WHERE character.movie_id = ? AND character.movie_owner_id = ? AND character.actor_id = actor.id")) {
            preparedStmt.setInt(1, movieId);
            preparedStmt.setInt(2, movieOwnerId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Character character = new Character(
                        result.getInt(1),
                        result.getString(2),
                        result.getInt(3),
                        new Actor(result.getInt(4), result.getString(5))
                );
                characterList.add(character);
            }
            return characterList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean create(String name, int movieId, int movieOwnerId, int actorId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO character(name, movie_id, movie_owner_id, actor_id) VALUES (?,?,?,?);")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, movieId);
            preparedStmt.setInt(3, movieOwnerId);
            preparedStmt.setInt(4, actorId);

            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Character> get(String name, int movieId, int movieOwnerId, int actorId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT id, name, movie_id, actor_id FROM character WHERE name = ? AND movie_id = ? AND actor_id = ? AND movie_owner_id = ?");
             PreparedStatement preparedStmt2 = conn.prepareStatement("SELECT id, name FROM actor WHERE id = ?")) {
            preparedStmt.setString(1, name);
            preparedStmt.setInt(2, movieId);
            preparedStmt.setInt(3, actorId);
            preparedStmt.setInt(4, movieOwnerId);
            preparedStmt2.setInt(1, actorId);
            ResultSet result = preparedStmt.executeQuery();
            ResultSet result2 = preparedStmt2.executeQuery();
            if (result.next()) {
                if (result2.next()) {
                    Character character = new Character(result.getInt(1), result.getString(2), result.getInt(3), new Actor(result.getInt(4), result2.getString(2)));
                    return Optional.of(character);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(int characterId, int movieOwnerId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM character WHERE id = ? AND movie_owner_id = ?;")) {
            preparedStmt.setInt(1, characterId);
            preparedStmt.setInt(2, movieOwnerId);
            int result = preparedStmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
