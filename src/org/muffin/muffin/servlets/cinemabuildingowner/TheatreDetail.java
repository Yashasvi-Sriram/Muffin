package org.muffin.muffin.servlets.cinemabuildingowner;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  sends all buildings of cinema_building_owner
 * doPostWithSession: same as get
 */
@WebServlet("/cinemabuildingowner/theatredetail")
public class TheatreDetail extends CinemaBuildingOwnerEnsuredSessionServlet {
    private CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();
    private TheatreDAO theatreDAO = new TheatreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int cinemaBuildingId = Integer.parseInt(request.getParameter("cinemaBuildingId"));
        Optional<CinemaBuilding> cinemaBuildingOpt = cinemaBuildingDAO.get(cinemaBuildingId);
        if (!cinemaBuildingOpt.isPresent()) {
            request.setAttribute("message", "The cinema building with id " + cinemaBuildingId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        List<Theatre> theatreList = theatreDAO.getByCinemaBuilding(cinemaBuildingId, cinemaBuildingOwner.getId());
        request.setAttribute("theatreList", theatreList);
        request.setAttribute("cinemaBuilding", cinemaBuildingOpt.get());
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/theatredetail.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

