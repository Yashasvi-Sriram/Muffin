package org.muffin.muffin.servlets.character;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.CharacterDAO;
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
 * doGetWithSession:  tries to create a new character with given params, if success returns created obj, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/character/create")
public class Create extends MovieOwnerEnsuredSessionServlet {
    private ActorDAO actorDAO = new ActorDAOImpl();
    private CharacterDAO characterDAO = new CharacterDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String characterName = request.getParameter("characterName");
        String actorName = request.getParameter("actorName");
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();

        // create actor if not already there
        Optional<Actor> existingActorOpt = actorDAO.get(actorName);
        int actorId;
        if (existingActorOpt.isPresent()) {
            actorId = existingActorOpt.get().getId();
        } else {
            Optional<Actor> newActor = actorDAO.create(actorName);
            if (newActor.isPresent()) {
                actorId = newActor.get().getId();
            } else {
                out.println(gson.toJson(ResponseWrapper.error("Critical Error! Cannot find or create actor!")));
                out.close();
                return;
            }
        }
        // map the actor and character
        Optional<Character> characterOpt = characterDAO.create(characterName, movieId, movieOwner.getId(), actorId);
        if (characterOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(characterOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The character name has to be unique in a movie")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
