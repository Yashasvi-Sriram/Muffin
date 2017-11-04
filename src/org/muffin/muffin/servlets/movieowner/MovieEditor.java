package org.muffin.muffin.servlets.movieowner;

import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * doGetWithSession:  renders movieowner's movieeditor page
 * doPostWithSession: same as get
 */
@WebServlet("/movieowner/movieeditor")
public class MovieEditor extends MovieOwnerEnsuredSessionServlet {
    MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        List<Movie> movieList = movieDAO.getByOwner(movieOwner.getId());
        request.setAttribute("movieList", movieList);
        request.getRequestDispatcher("/WEB-INF/jsps/movieowner/movieeditor.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
