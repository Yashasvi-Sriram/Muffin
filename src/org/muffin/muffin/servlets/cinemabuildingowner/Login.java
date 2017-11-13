package org.muffin.muffin.servlets.cinemabuildingowner;

import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.CinemaBuildingOwnerDAOImpl;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingOwnerDAO;
import org.muffin.muffin.daos.MuffDAO;
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
 * doGet: renders cinema_building_owner login page
 * doPost: if auth success redirect to cinema_building_owner home, else print error in login page
 */
@WebServlet("/cinemabuildingowner/login")
public class Login extends HttpServlet {
    private CinemaBuildingOwnerDAO cinemaBuildingOwnerDAO = new CinemaBuildingOwnerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", request.getContextPath() + "/cinemabuildingowner/login");
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/login.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String handle = request.getParameter("handle");
        String password = request.getParameter("password");
        if (cinemaBuildingOwnerDAO.exists(handle, password)) {
            // If session already exists remove invalidate it
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            Optional<CinemaBuildingOwner> cinemaBuildingOwnerOpt = cinemaBuildingOwnerDAO.get(handle);
            cinemaBuildingOwnerOpt.ifPresent(cinemaBuildingOwner -> newSession.setAttribute(SessionKeys.CINEMA_BUILDING_OWNER, cinemaBuildingOwner));
            response.sendRedirect(request.getContextPath() + "/cinemabuildingowner/home");
        } else {
            request.setAttribute("message", "Error! Hint: Check Credentials");
            request.setAttribute("action", request.getContextPath() + "/cinemabuildingowner/login");
            request.setAttribute("handle", handle);
            request.setAttribute("password", password);
            request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/login.jsp").include(request, response);
        }
    }
}
