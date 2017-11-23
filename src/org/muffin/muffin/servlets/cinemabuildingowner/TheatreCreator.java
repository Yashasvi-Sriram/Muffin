package org.muffin.muffin.servlets.cinemabuildingowner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Theatre;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.SeatDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.SeatDAO;
import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * doGetWithSession:  renders theatre creator
 * doPostWithSession: tries to create a new theatre with given params
 */
@WebServlet("/cinemabuildingowner/theatrecreator")
public class TheatreCreator extends CinemaBuildingOwnerEnsuredSessionServlet {
    private SeatDAO seatDAO = new SeatDAOImpl();
    private TheatreDAO theatreDAO = new TheatreDAOImpl();
    private CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int cinemaBuildingId = Integer.parseInt(request.getParameter("cinemaBuildingId"));
        Optional<CinemaBuilding> cinemaBuildingOpt = cinemaBuildingDAO.get(cinemaBuildingId);
        if (!cinemaBuildingOpt.isPresent()) {
            request.setAttribute("message", "The cinema building with id " + cinemaBuildingId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        request.setAttribute("cinemaBuilding", cinemaBuildingOpt.get());
        request.getRequestDispatcher("/WEB-INF/jsps/cinemabuildingowner/theatrecreator.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int cinemaBuildingId = Integer.parseInt(request.getParameter("cinemaBuildingId"));
        int screenNo = Integer.parseInt(request.getParameter("screenNo"));
        Gson gson = new GsonBuilder().create();
        List<Integer> seats = gson.fromJson(request.getParameter("seats"), new TypeToken<List<Integer>>() {
        }.getType());
        if (seats.size() % 2 != 0) {
            request.setAttribute("message", "Seats data is invalid");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        Set<Pair<Integer, Integer>> seatsXY = new HashSet<>();
        for (int i = 0; i < seats.size(); i += 2) {
            seatsXY.add(new Pair<>(seats.get(i), seats.get(i + 1)));
        }

        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        Optional<Theatre> theatreOpt = theatreDAO.create(cinemaBuildingId, screenNo, cinemaBuildingOwner.getId());
        if (!theatreOpt.isPresent()) {
            request.setAttribute("message", "The theatre could not be created");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        if (!seatDAO.createSeatsOfTheatre(theatreOpt.get().getId(), seatsXY)) {
            request.setAttribute("message", "The seats could not be created for the theatre");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/cinemabuildingowner/theatreeditor?cinemaBuildingId=" + cinemaBuildingId);
    }
}
