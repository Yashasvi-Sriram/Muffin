package org.muffin.muffin.servlets.movie;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * doGetWithSession:  renders movie profile page
 * doPostWithSession: same as get
 */
@WebServlet("/movie/profile")
public class Profile extends MuffEnsuredSessionServlet {
    private MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int profileMovieId = Integer.parseInt(request.getParameter("movieId"));
        Optional<Movie> profileMovieOpt = movieDAO.get(profileMovieId);
        if (!profileMovieOpt.isPresent()) {
            request.setAttribute("message", "The movie with id " + profileMovieId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        Movie profileMovie = profileMovieOpt.get();
        Optional<Float> averageRatingOpt = movieDAO.getAverageRating(profileMovieId);
        if (!averageRatingOpt.isPresent()) {
            request.setAttribute("message", "The movie with id " + profileMovieId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        float averageRating = averageRatingOpt.get();
        Optional<Integer> reviewCountOpt = movieDAO.getReviewCount(profileMovieId);
        if (!reviewCountOpt.isPresent()) {
            request.setAttribute("message", "The movie with id " + profileMovieId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        int reviewCount = reviewCountOpt.get();
        Map<Integer, Integer> ratingHistogram = movieDAO.getRatingHistogram(profileMovieId);
        request.setAttribute("profileMovie", profileMovie);
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("reviewCount", reviewCount);
        request.setAttribute("ratingHistogram", ratingHistogram);
        request.getRequestDispatcher("/WEB-INF/jsps/movie/profile.jsp").include(request, response);

    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
