package org.muffin.muffin.servlets.movieowner.movieeditor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.muffin.muffin.beans.Actor;
import org.muffin.muffin.beans.Character;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.ActorDAOImpl;
import org.muffin.muffin.daoimplementations.CharacterDAOImpl;
import org.muffin.muffin.daos.ActorDAO;
import org.muffin.muffin.daos.CharacterDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;

import com.google.gson.GsonBuilder;


/**
 * Servlet implementation class MovieInfo
 */
@WebServlet("/movieowner/actorsearch")
public class ActorSearch extends MovieOwnerEnsuredSessionServlet {
    ActorDAO actorDAO = new ActorDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    	System.out.println("Hey");
    	 String searchKey = request.getParameter("searchKey");
         List<Actor> actors = actorDAO.search(searchKey);
         PrintWriter out = response.getWriter();
         
         out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(actors, ResponseWrapper.ARRAY_RESPONSE)));
         out.close();	
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    	
    }
}

