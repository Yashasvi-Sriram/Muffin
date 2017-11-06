package org.muffin.muffin.servlets.movie;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * doGetWithSession:  returns matched movies
 * doPostWithSession: same as get
 */
@WebServlet("/movie/search")
public class Search extends EnsuredSessionServlet {
    MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String pattern = request.getParameter("pattern");
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<Movie> movies = movieDAO.search(pattern, offset, limit);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(movies, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

