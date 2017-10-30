package org.muffin.muffin.servlets.movieowner;

import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.MovieOwnerDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.MovieOwnerDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/movieowneraddmovie")
public class AddMovie extends EnsuredSessionServlet {
	MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
//        request.setAttribute("action", request.getContextPath() + "/movieownerlogin");
//        request.getRequestDispatcher("WEB-INF/jsps/movieowner/login.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String moviename = request.getParameter("moviename");
        Integer durationInMinutes = Integer.parseInt(request.getParameter("duration"));
        MovieOwner movieOwner = (MovieOwner) session.getAttribute(SessionKeys.MOVIE_OWNER);
        PrintWriter out = response.getWriter();
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        JsonObject obj = new JsonObject();
        if(movieDAO.create(moviename, durationInMinutes, movieOwner.getId())) {
        	obj.addProperty("status", "success");
        	obj.addProperty("moviename", moviename);
        	obj.addProperty("duration", Integer.toString(durationInMinutes));
        	
        	
        	
        }
        else {
        	obj.addProperty("status", "failure");
        	obj.addProperty("message", "Addition unsuccesful");
        }
        
        out.println(gson.toJson(obj));
        
//        if (movieOwnerDAO.exists(handle, password)) {
//            // If session already exists remove invalidate it
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//                session.invalidate();
//            }
//            HttpSession newSession = request.getSession(true);
//            Optional<MovieOwner> movieOwnerOpt = movieOwnerDAO.get(handle);
//            movieOwnerOpt.ifPresent(movieOwner -> newSession.setAttribute(SessionKeys.MOVIE_OWNER, movieOwner));
//            response.sendRedirect(request.getContextPath() + "/movieownerhome");
//        } else {
//            request.setAttribute("message", "Invalid Credentials");
//            request.setAttribute("action", request.getContextPath() + "/movieownerlogin");
//            request.setAttribute("handle", handle);
//            request.setAttribute("password", password);
//            request.getRequestDispatcher("WEB-INF/jsps/movieowner/login.jsp").include(request, response);
//        }
        
        
    }
}
