package org.muffin.muffin.servlets.muff;

import org.muffin.muffin.beans.Booking;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seat;
import org.muffin.muffin.daoimplementations.BookingDAOImpl;
import org.muffin.muffin.daos.BookingDAO;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * doGetWithSession:  renders muff booking history
 * doPostWithSession: same as get
 */
@WebServlet("/muff/bookinghistory")
public class BookingHistory extends MuffEnsuredSessionServlet {
    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        List<Booking> bookingHistory = bookingDAO.getMuffBookingHistory(muff.getId());
        request.setAttribute("bookingHistory", bookingHistory);
        request.getRequestDispatcher("/WEB-INF/jsps/muff/bookinghistory.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
