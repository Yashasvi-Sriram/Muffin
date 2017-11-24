package org.muffin.muffin.servlets.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seat;
import org.muffin.muffin.beans.Theatre;
import org.muffin.muffin.daoimplementations.BookingDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.BookingDAO;
import org.muffin.muffin.daos.TheatreDAO;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
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
 * doGetWithSession:  renders show book page
 * doPostWithSession: creates a booking using the received params
 */
@WebServlet("/show/book")
public class Book extends MuffEnsuredSessionServlet {
    private TheatreDAO theatreDAO = new TheatreDAOImpl();
    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int theatreId = Integer.parseInt(request.getParameter("theatreId"));
        int showId = Integer.parseInt(request.getParameter("showId"));
        Optional<Theatre> theatreOpt = theatreDAO.getById(theatreId);
        if (!theatreOpt.isPresent()) {
            request.setAttribute("message", "The theatre with id " + theatreId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        List<Seat> theatreSeats = theatreDAO.getSeatsOf(theatreId);
        List<Seat> alreadyBookedSeats = bookingDAO.getBookedSeats(showId);
        Gson gson = new GsonBuilder().create();
        request.setAttribute("showId", showId);
        request.setAttribute("theatre", gson.toJson(theatreOpt.get()));
        request.setAttribute("theatreSeats", gson.toJson(theatreSeats));
        request.setAttribute("alreadyBookedSeats", gson.toJson(alreadyBookedSeats));
        request.getRequestDispatcher("/WEB-INF/jsps/show/book.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int showId = Integer.parseInt(request.getParameter("showId"));
        String seatsToBeBookedStr = request.getParameter("seatsToBeBooked");
        Gson gson = new GsonBuilder().create();
        List<Seat> seatsToBeBooked = gson.fromJson(seatsToBeBookedStr, new TypeToken<List<Seat>>() {
        }.getType());
        Muff inSessionMuff = (Muff) session.getAttribute(SessionKeys.MUFF);
        if (bookingDAO.create(showId, inSessionMuff.getId(), seatsToBeBooked)) {
            response.sendRedirect(request.getContextPath() + "/muff/home");
        } else {
            request.setAttribute("message", "The booking could not be done");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
        }
    }
}
