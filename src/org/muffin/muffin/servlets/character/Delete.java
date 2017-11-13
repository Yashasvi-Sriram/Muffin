package org.muffin.muffin.servlets.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daos.CharacterDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * doGetWithSession:  tries to delete a character in a movie with given id, if success returns 0, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/character/delete")
public class Delete extends MovieOwnerEnsuredSessionServlet {
    private CharacterDAO characterDAO = new CharacterDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int characterId = Integer.parseInt(request.getParameter("id"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (characterDAO.delete(characterId, movieOwner.getId())) {
            out.println(gson.toJson(ResponseWrapper.get(0, ResponseWrapper.NUMBER_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error!")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
