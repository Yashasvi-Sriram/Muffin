package org.muffin.muffin.servlets.genre;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Genre;
import org.muffin.muffin.daoimplementations.GenreDAOImpl;
import org.muffin.muffin.daos.GenreDAO;
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
 * doGetWithSession:  returns all genres
 * doPostWithSession: same as get
 */
@WebServlet("/genre/fetch/all")
public class FetchAll extends EnsuredSessionServlet {
    private GenreDAO genreDAO = new GenreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        List<Genre> genres = genreDAO.getAll();
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(genres, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

