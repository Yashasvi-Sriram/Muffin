package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.beans.Character;

import org.muffin.muffin.daos.CharacterDAO;

import org.muffin.muffin.db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CharacterDAOImpl implements CharacterDAO {
    @Override
    public List<Character> getByMovie(int movieId) {
        List<Character> characterList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);
             PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM character,actor WHERE character.movie_id = ? and character.actor_id = actor.id")) {
            preparedStmt.setInt(1, movieId);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Character character = new Character(result.getInt(1),  result.getString(2),result.getInt(3), result.getInt(4), result.getString(6));
                characterList.add(character);
            }
            return characterList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
