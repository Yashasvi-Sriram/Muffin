package org.muffin.muffin.servlets.genre;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.GenreDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;
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
 * doGetWithSession:  tries to delete a genre with given params, if success returns 0
 * doPostWithSession: same as GET
 */
@WebServlet("/genre/delete")
public class Delete extends MovieOwnerEnsuredSessionServlet {
    GenreDAO genreDAO = new GenreDAOImpl();
    MovieDAO movieDAO = new MovieDAOImpl();


    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        int movieId = Integer.parseInt(request.getParameter("movieId"));
        int genreId = Integer.parseInt(request.getParameter("id"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();


        if (movieDAO.updateGenre(movieId, movieOwner.getId(), genreId, 0)) {

            out.println(gson.toJson(ResponseWrapper.get(0, ResponseWrapper.NUMBER_RESPONSE)));
        } else {
            System.out.println("Critical error!");
            out.println(gson.toJson(ResponseWrapper.error("Error!")));
        }


        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
