package org.muffin.muffin.servlets.genre;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.daoimplementations.GenreDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.CharacterDAO;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * doGetWithSession:  tries to ad a new genre to the movie with given params, if success returns created obj, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/genre/create")
public class Create extends MovieOwnerEnsuredSessionServlet {
    GenreDAO genreDAO = new GenreDAOImpl();
    MovieDAO movieDAO = new MovieDAOImpl();


    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String genreName = request.getParameter("genreName");
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        Optional<Genre> genreOpt = genreDAO.get(genreName);
        if (genreOpt.isPresent()) {

            if (movieDAO.updateGenre(movieId, movieOwner.getId(), genreOpt.get().getId(), 1)) {

                out.println(gson.toJson(ResponseWrapper.get(genreOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
            } else {
                System.out.println("Critical error!");
                out.println(gson.toJson(ResponseWrapper.error("Error!")));
            }
        } else {

            out.println(gson.toJson(ResponseWrapper.error("Error! Type Correct Genre Name")));
        }


        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
