package org.muffin.muffin.servlets.movie;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieStats;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.GenreDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * doGetWithSession:  renders movie profile page
 * doPostWithSession: same as get
 */
@WebServlet("/movie/stats")
public class Stats extends EnsuredSessionServlet {
    private MovieDAO movieDAO = new MovieDAOImpl();
    private GenreDAO genreDAO = new GenreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        Optional<Movie> movieOpt = movieDAO.get(movieId);
        if (movieOpt.isPresent()) {
            List<Genre> genres = genreDAO.getByMovie(movieId);
            float averageRating = movieDAO.getAverageRating(movieId);
            int userCount = movieDAO.getUserCount(movieId);
            Map<Integer,Integer> ratingHistogram = movieDAO.getRatingHistogram(movieId);
            MovieStats movieStats = new MovieStats(genres,averageRating,userCount,ratingHistogram);
            PrintWriter out = response.getWriter();
            out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(movieStats, ResponseWrapper.OBJECT_RESPONSE)));
            out.close();




        } else {
            request.setAttribute("message", "The movie with id " + movieId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
        }
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
