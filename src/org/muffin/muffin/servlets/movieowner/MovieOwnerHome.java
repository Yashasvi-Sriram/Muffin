package org.muffin.muffin.servlets.movieowner;

import org.muffin.muffin.daoimplementations.MovieOwnerDAOImpl;
import org.muffin.muffin.daos.MovieOwnerDAO;
import org.muffin.muffin.servlets.EnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/MovieOwnerHome")
public class MovieOwnerHome extends EnsuredSessionServlet {
    MovieOwnerDAO movieOwnerDAO = new MovieOwnerDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
