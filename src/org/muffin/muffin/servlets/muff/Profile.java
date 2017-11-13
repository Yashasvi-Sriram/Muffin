package org.muffin.muffin.servlets.muff;

import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * doGetWithSession:  renders muff profile page
 * doPostWithSession: same as get
 */
@WebServlet("/muff/profile")
public class Profile extends MuffEnsuredSessionServlet {
    private MuffDAO muffDAO = new MuffDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int profileMuffId = Integer.parseInt(request.getParameter("muffId"));
        Optional<Muff> profileMuffOpt = muffDAO.get(profileMuffId);
        if (profileMuffOpt.isPresent()) {
            Muff profileMuff = profileMuffOpt.get();
            request.setAttribute("profileMuff", profileMuff);
            request.getRequestDispatcher("/WEB-INF/jsps/muff/profile.jsp").include(request, response);
        } else {
            request.setAttribute("message", "The muff with id " + profileMuffId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
        }
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
