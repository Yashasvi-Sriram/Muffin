package org.muffin.muffin.servlets.cinemabuildingowner;

import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.MovieOwner;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * doGetWithSession:  sends all buildings of cinema_building_owner
 * doPostWithSession: same as get
 */
@WebServlet("/cinemabuildingowner/buildinglist")
public class BuildingList extends CinemaBuildingOwnerEnsuredSessionServlet {
    CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        List<CinemaBuilding> cinemaBuildingList = cinemaBuildingDAO.getByOwner(cinemaBuildingOwner.getId());
        request.setAttribute("cinemaBuildingList", cinemaBuildingList);
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/cinemabuildinglist.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
