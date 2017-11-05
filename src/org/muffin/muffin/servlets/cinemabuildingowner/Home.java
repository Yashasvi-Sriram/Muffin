package org.muffin.muffin.servlets.cinemabuildingowner;

import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * doGetWithSession:  renders cinema_building_owner home page
 * doPostWithSession: same as get
 */
@WebServlet("/cinemabuildingowner/home")
public class Home extends CinemaBuildingOwnerEnsuredSessionServlet {
    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/home.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
