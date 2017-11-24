package org.muffin.muffin;


import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daoimplementations.SeekDAOImpl;
import org.muffin.muffin.daos.SeekDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scratch {
    public static void main(String[] args) {
        SeekDAO seekDAO = new SeekDAOImpl();
        List<Genre> genres = new ArrayList<>();
        Genre temp1 = new Genre(14,"Action");
        Genre temp2 = new Genre(18,"Thriller");
        genres.add(temp1);
        genres.add(temp2);
        Optional<Movie> ret = seekDAO.getAutomatedSuggestion(genres);
        if(ret.isPresent()) {
            System.out.println(ret.get().getName());
            List<Genre> temp = ret.get().getGenres();
            for(int i=0;i < temp.size();i++) {
                System.out.println(temp.get(i).getName());
            }
        }
        else {
            System.out.println("No suggestions");
        }
    }
}
