package org.muffin.muffin.servlets.suggestion;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Review;
import org.muffin.muffin.daoimplementations.ReviewDAOImpl;
import org.muffin.muffin.daoimplementations.SuggestionDAOImpl;
import org.muffin.muffin.daos.ReviewDAO;
import org.muffin.muffin.daos.SuggestionDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

/**
 * doGetWithSession:  returns suggested movies;
 * doPostWithSession: same as get
 */
@WebServlet("/fetch/movies")
public class FetchMovies extends MuffEnsuredSessionServlet {
    private SuggestionDAO suggestionDAO = new SuggestionDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        int muffId = muff.getId();
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<Movie> movies = suggestionDAO.getMovies(muffId,limit);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(movies, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
