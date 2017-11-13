package org.muffin.muffin.servlets.actor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;

import com.google.gson.GsonBuilder;

/**
 * doGetWithSession:  returns matched actors
 * doPostWithSession: same as get
 */
@WebServlet("/actor/search")
public class Search extends EnsuredSessionServlet {
    private ActorDAO actorDAO = new ActorDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String pattern = request.getParameter("pattern");
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<Actor> actors = actorDAO.search(pattern, offset, limit);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(actors, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

