package org.muffin.muffin.servlets.movieowner;

import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.MovieOwnerDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.MovieOwnerDAO;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/movieownerhome")
public class Home extends EnsuredSessionServlet {
    MovieOwnerDAO movieOwnerDAO = new MovieOwnerDAOImpl();
    MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        List<Movie> movies = movieDAO.getByOwner((Integer) session.getAttribute(SessionKeys.MOVIE_OWNER_ID));
        System.out.println(movies);
        request.getRequestDispatcher("WEB-INF/jsps/movieowner/home.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
