package org.muffin.muffin.servlets.movieowner;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.muffin.muffin.beans.Character;

import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;

import org.muffin.muffin.daoimplementations.GenreDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.CharacterDAO;

import org.muffin.muffin.daos.GenreDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

/**
 * doGetWithSession:  renders movie detail page in movieowner's perspective
 * doPostWithSession: same as get
 */
@WebServlet("/movieowner/moviedetail")
public class MovieDetail extends MovieOwnerEnsuredSessionServlet {
    private CharacterDAO characterDAO = new CharacterDAOImpl();
    private GenreDAO genreDAO = new GenreDAOImpl();
    private MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        Optional<Movie> movieOpt = movieDAO.get(movieId);
        if (!movieOpt.isPresent()) {
            request.setAttribute("message", "The movie with id " + movieId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        List<Character> characterList = characterDAO.getByMovie(movieId, movieOwner.getId());
        List<Genre> genreList = genreDAO.getByMovie(movieId);
        request.setAttribute("characterList", characterList);
        request.setAttribute("genreList", genreList);
        request.setAttribute("movie", movieOpt.get());
        request.setAttribute("movieId", movieId);
        request.getRequestDispatcher("/WEB-INF/jsps/movieowner/moviedetail.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
