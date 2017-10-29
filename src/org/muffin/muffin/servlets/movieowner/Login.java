package org.muffin.muffin.servlets.movieowner;

import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.servlets.SessionKeys;
import org.muffin.muffin.daoimplementations.MovieOwnerDAOImpl;
import org.muffin.muffin.daos.MovieOwnerDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/movieownerlogin")
public class Login extends HttpServlet {
    MovieOwnerDAO movieOwnerDAO = new MovieOwnerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", "./movieownerlogin");
        request.getRequestDispatcher("WEB-INF/jsps/movieowner/login.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String handle = request.getParameter("handle");
        String password = request.getParameter("password");
        if (movieOwnerDAO.exists(handle, password)) {
            // If session already exists remove invalidate it
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            Optional<MovieOwner> movieOwnerOpt = movieOwnerDAO.get(handle);
            movieOwnerOpt.ifPresent(movieOwner -> newSession.setAttribute(SessionKeys.MOVIE_OWNER, movieOwner));
            response.sendRedirect("./movieownerhome");
        } else {
            request.setAttribute("message", "Invalid Credentials");
            request.setAttribute("action", "./movieownerlogin");
            request.setAttribute("handle", handle);
            request.setAttribute("password", password);
            request.getRequestDispatcher("WEB-INF/jsps/movieowner/login.jsp").include(request, response);
        }
    }
}
