package org.muffin.muffin.servlets.review;

import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Review;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.ReviewDAOImpl;
import org.muffin.muffin.daos.CharacterDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.ReviewDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * doGetWithSession:  same as Post
 * doPostWithSession: Adds a review posted by the user, returns review object on success
 */
@WebServlet("/review/create")
public class Create extends MuffEnsuredSessionServlet {
    private MovieDAO movieDAO = new MovieDAOImpl();
    private ReviewDAO reviewDAO = new ReviewDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doPostWithSession(request, response, session);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String movieName = request.getParameter("name");
        String textReview = request.getParameter("textReview");
        float rating = Float.parseFloat(request.getParameter("rating"));
        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        int muffId = muff.getId();

        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        Optional<Movie> movieOpt = movieDAO.get(movieName);
        if (movieOpt.isPresent()) {
            int movieId = movieOpt.get().getId();
            Optional<Review> reviewOpt = reviewDAO.get(movieId, muffId);
            if (reviewOpt.isPresent()) {
                out.println(gson.toJson(ResponseWrapper.error("Error! You can give only a single review for this movie")));
            } else {
                if (reviewDAO.create(movieId, muffId, rating, textReview)) {
                    out.println(gson.toJson(ResponseWrapper.get("Review is posted", ResponseWrapper.STRING_RESPONSE)));
                } else {
                    out.println(gson.toJson(ResponseWrapper.error("Error!")));
                }
            }
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! The Movie name doesn't exist")));
        }
        out.close();
    }
}
