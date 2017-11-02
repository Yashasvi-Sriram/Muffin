package org.muffin.muffin.servlets.movieowner.movieeditor;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.muffin.muffin.beans.Character;

import org.muffin.muffin.daoimplementations.CharacterDAOImpl;

import org.muffin.muffin.daos.CharacterDAO;

import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;


/**
 * Servlet implementation class MovieInfo
 */
@WebServlet("/movieowner/movieinfo")
public class MovieInfo extends MovieOwnerEnsuredSessionServlet {
    CharacterDAO characterDAO = new CharacterDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    		doPostWithSession(request,response,session);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    	int movieId = Integer.parseInt(request.getParameter("movieId"));
    	List<Character> characterList = characterDAO.getByMovie(movieId);
    	System.out.println("hey");
    	System.out.println(characterList.get(0).toString());
        request.setAttribute("characterList", characterList);
        request.getRequestDispatcher("/WEB-INF/jsps/movieowner/movieinfo.jsp").include(request, response);
    }
}
