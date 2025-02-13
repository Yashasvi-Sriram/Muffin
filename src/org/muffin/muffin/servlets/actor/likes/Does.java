package org.muffin.muffin.servlets.actor.likes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * doGetWithSession:  returns if muff likes actor
 * doPostWithSession: same as get
 */
@WebServlet("/actor/likes/does")
public class Does extends MuffEnsuredSessionServlet {
    private ActorDAO actorDAO = new ActorDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int muffId = ((Muff) session.getAttribute(SessionKeys.MUFF)).getId();
        int actorId = Integer.parseInt(request.getParameter("actorId"));
        Optional<Boolean> doesLikes = actorDAO.doesLikes(muffId, actorId);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (doesLikes.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(doesLikes.get(), ResponseWrapper.BOOLEAN_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Server problem!")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
