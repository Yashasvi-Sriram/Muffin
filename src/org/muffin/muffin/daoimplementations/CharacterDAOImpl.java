package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daos.CharacterDAO;

import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.*;


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
    public List<Character> getByMovie(int movieId) {

        List<Character> characterList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT character.id, character.name, character.movie_id, actor.id, actor.name FROM character, actor WHERE character.movie_id = ? AND character.actor_id = actor.id")) {
            preparedStmt.setInt(1, movieId);
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
    public Optional<Character> create(String name, int movieId, int movieOwnerId, int actorId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement createCharacter = conn.prepareStatement("INSERT INTO character(name, movie_id, movie_owner_id, actor_id) VALUES (?,?,?,?) RETURNING id, name, movie_id, actor_id;");
             PreparedStatement getActor = conn.prepareStatement("SELECT id, name FROM actor WHERE id = ?");) {
            createCharacter.setString(1, name);
            createCharacter.setInt(2, movieId);
            createCharacter.setInt(3, movieOwnerId);
            createCharacter.setInt(4, actorId);

            ResultSet characterRS = createCharacter.executeQuery();

            getActor.setInt(1, actorId);
            ResultSet actorRS = getActor.executeQuery();
            if (characterRS.next()) {
                if (actorRS.next()) {
                    Character character = new Character(characterRS.getInt(1), characterRS.getString(2), characterRS.getInt(3), new Actor(characterRS.getInt(4), actorRS.getString(2)));
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
    public Optional<Character> get(String name, int movieId, int movieOwnerId, int actorId) {
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement getCharacter = conn.prepareStatement("SELECT id, name, movie_id, actor_id FROM character WHERE name = ? AND movie_id = ? AND actor_id = ? AND movie_owner_id = ?");
             PreparedStatement getActor = conn.prepareStatement("SELECT id, name FROM actor WHERE id = ?")) {
            getCharacter.setString(1, name);
            getCharacter.setInt(2, movieId);
            getCharacter.setInt(3, actorId);
            getCharacter.setInt(4, movieOwnerId);
            getActor.setInt(1, actorId);
            ResultSet characterRS = getCharacter.executeQuery();
            ResultSet actorRS = getActor.executeQuery();
            if (characterRS.next()) {
                if (actorRS.next()) {
                    Character character = new Character(characterRS.getInt(1), characterRS.getString(2), characterRS.getInt(3), new Actor(characterRS.getInt(4), actorRS.getString(2)));
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
