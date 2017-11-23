package org.muffin.muffin.servlets.cinemabuildingowner;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.ShowDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.ShowDAO;
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
 * doGetWithSession:  sends all shows of theatre
 * doPostWithSession: same as get
 */
@WebServlet("/cinemabuildingowner/showdetail")
public class ShowDetail extends CinemaBuildingOwnerEnsuredSessionServlet {
    private ShowDAO showDAO = new ShowDAOImpl();
    private TheatreDAO theatreDAO = new TheatreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int theatreId = Integer.parseInt(request.getParameter("theatreId"));
        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        Optional<Theatre> theatreOpt = theatreDAO.getByOwner(theatreId, cinemaBuildingOwner.getId());
        if (!theatreOpt.isPresent()) {
            request.setAttribute("message", "The theatre with id " + theatreId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }

        List<Show> showList = showDAO.get(theatreOpt.get().getId());

        request.setAttribute("theatre", theatreOpt.get());
        request.setAttribute("showList", showList);
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/showdetail.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

