package org.muffin.muffin.servlets.movie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
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
 * doGetWithSession:  tries to update a new movie with given id, if success returns updated object, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/movie/update")
public class Update extends MovieOwnerEnsuredSessionServlet {
    MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int movieId = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int durationInMinutes = Integer.parseInt(request.getParameter("durationInMinutes"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (movieDAO.update(movieId, movieOwner.getId(), name, durationInMinutes)) {
            Optional<Movie> movieOpt = movieDAO.get(name);
            if (movieOpt.isPresent()) {
                out.println(gson.toJson(ResponseWrapper.get(movieOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
            } else {
                System.out.println("Critical error!");
                out.println(gson.toJson(ResponseWrapper.error("Error!")));
            }
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: All movie names must be unique")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
