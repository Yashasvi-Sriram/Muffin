package org.muffin.muffin.servlets.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Seat;
import org.muffin.muffin.beans.Theatre;
import org.muffin.muffin.daoimplementations.BookingDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.BookingDAO;
import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  renders show book page
 * doPostWithSession: same as get
 */
@WebServlet("/show/book")
public class Book extends MuffEnsuredSessionServlet {
    TheatreDAO theatreDAO = new TheatreDAOImpl();
    BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int theatreId = Integer.parseInt(request.getParameter("theatreId"));
        Optional<Theatre> theatreOpt = theatreDAO.getById(theatreId);
        if (!theatreOpt.isPresent()) {
            request.setAttribute("message", "The theatre with id " + theatreId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        List<Seat> theatreSeats = theatreDAO.getSeatsOf(theatreId);
        Gson gson = new GsonBuilder().create();
        request.setAttribute("theatre", gson.toJson(theatreOpt.get()));
        request.setAttribute("theatreSeats", gson.toJson(theatreSeats));
        request.getRequestDispatcher("/WEB-INF/jsps/show/book.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
