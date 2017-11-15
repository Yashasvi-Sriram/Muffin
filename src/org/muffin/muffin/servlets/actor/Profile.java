package org.muffin.muffin.servlets.actor;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.CharacterDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * doGetWithSession:  renders actor profile page
 * doPostWithSession: same as get
 */
@WebServlet("/actor/profile")
public class Profile extends MuffEnsuredSessionServlet {
    private ActorDAO actorDAO = new ActorDAOImpl();


    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int profileActorId = Integer.parseInt(request.getParameter("actorId"));
        Optional<Actor> profileActorOpt = actorDAO.get(profileActorId);

        if (!profileActorOpt.isPresent()) {
            request.setAttribute("message", "The actor with id " + profileActorId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        Actor profileActor = profileActorOpt.get();
        Map<Genre, Integer> genreHistogram = actorDAO.getGenreMovieHistogram(profileActorId);
        Map<Integer, String> movieMap = actorDAO.getAllMovies(profileActorId);
        request.setAttribute("profileActor", profileActor);
        request.setAttribute("genreHistogram", genreHistogram);
        request.setAttribute("movieMap", movieMap);
        request.getRequestDispatcher("/WEB-INF/jsps/actor/profile.jsp").include(request, response);

    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
