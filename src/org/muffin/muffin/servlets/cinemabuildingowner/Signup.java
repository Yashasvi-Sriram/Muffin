package org.muffin.muffin.servlets.cinemabuildingowner;

import lombok.NonNull;

import org.muffin.muffin.daoimplementations.CinemaBuildingOwnerDAOImpl;

import org.muffin.muffin.daos.CinemaBuildingOwnerDAO;

import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * doGet: renders cinema_building_owner signup page
 * doPost: if signup success redirect to cinema_building_owner login, else print error in signup page
 */
@WebServlet("/cinemabuildingowner/signup")
public class Signup extends HttpServlet {
    private CinemaBuildingOwnerDAO cinemaBuildingOwnerDAO = new CinemaBuildingOwnerDAOImpl();

    private boolean isValid(String s) {
        return s != null && s.length() != 0 && s.length() <= 50;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", request.getContextPath() + "/cinemabuildingowner/signup");
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/signup.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String handle = request.getParameter("handle");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        if (!(isValid(handle) && isValid(name) && isValid(password))) {
            request.setAttribute("message", "Error! Please fill all the fields!");
        } else if (!cinemaBuildingOwnerDAO.create(handle, name, password).isPresent()) {
            request.setAttribute("message", "Error! Handle already exists!");
        } else {
            response.sendRedirect(request.getContextPath() + "/cinemabuildingowner/login");
            return;
        }
        request.setAttribute("action", request.getContextPath() + "/cinemabuildingowner/signup");
        request.setAttribute("handle", handle);
        request.setAttribute("name", name);
        request.setAttribute("password", password);
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/signup.jsp").include(request, response);
    }
}
