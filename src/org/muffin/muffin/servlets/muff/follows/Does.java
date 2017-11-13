package org.muffin.muffin.servlets.muff.follows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
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
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  returns if follower follows followee
 * doPostWithSession: same as get
 */
@WebServlet("/muff/follows/does")
public class Does extends MuffEnsuredSessionServlet {
    private MuffDAO muffDAO = new MuffDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int followerId = ((Muff) session.getAttribute(SessionKeys.MUFF)).getId();
        int followeeId = Integer.parseInt(request.getParameter("followeeId"));
        Optional<Boolean> doesFollows = muffDAO.doesFollows(followerId, followeeId);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (doesFollows.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(doesFollows.get(), ResponseWrapper.BOOLEAN_RESPONSE)));
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

