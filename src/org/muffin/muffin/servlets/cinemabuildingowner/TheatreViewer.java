package org.muffin.muffin.servlets.cinemabuildingowner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * doGetWithSession:  renders theatre creator
 * doPostWithSession: tries to create a new theatre with given params
 */
@WebServlet("/cinemabuildingowner/theatreviewer")
public class TheatreViewer extends CinemaBuildingOwnerEnsuredSessionServlet {
    private TheatreDAO theatreDAO = new TheatreDAOImpl();
    private CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int cinemaBuildingId = Integer.parseInt(request.getParameter("cinemaBuildingId"));
        int theatreId = Integer.parseInt(request.getParameter("theatreId"));
        int screenNo = Integer.parseInt(request.getParameter("screenNo"));
        Optional<CinemaBuilding> cinemaBuildingOpt = cinemaBuildingDAO.get(cinemaBuildingId);
        if (!cinemaBuildingOpt.isPresent()) {
            request.setAttribute("message", "The cinema building with id " + cinemaBuildingId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        Gson gson = new GsonBuilder().create();
        request.setAttribute("cinemaBuilding", cinemaBuildingOpt.get());
        request.setAttribute("screenNo", screenNo);
        request.setAttribute("seats", gson.toJson(theatreDAO.getSeatsOf(theatreId)));
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/theatreviewer.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
