package org.muffin.muffin.servlets.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.ShowDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.ShowDAO;
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
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * doGetWithSession:  tries to create a new show with given params, if success returns created obj, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/show/create")
public class Create extends CinemaBuildingOwnerEnsuredSessionServlet {
    private TheatreDAO theatreDAO = new TheatreDAOImpl();
    private MovieDAO movieDAO = new MovieDAOImpl();
    private ShowDAO showDAO = new ShowDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        int theatreId = Integer.parseInt(request.getParameter("theatreId"));
        String movieName = request.getParameter("movieName");
        String startDateTime = request.getParameter("startDateTime");
        String endDateTime = request.getParameter("endDateTime");
        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        Optional<Theatre> theatreOpt = theatreDAO.getByOwner(theatreId, cinemaBuildingOwner.getId());
        if (!theatreOpt.isPresent()) {
            request.setAttribute("message", "The theatre with id = " + theatreId + " does not exist");
            request.getRequestDispatcher("/WEB-INF/jsps/error.jsp").include(request, response);
            return;
        }
        Optional<Movie> movieOpt = movieDAO.get(movieName);
        if (movieOpt.isPresent()) {
            int movieId = movieOpt.get().getId();
            Showtime showtime = new Showtime(LocalDateTime.parse(startDateTime), LocalDateTime.parse(endDateTime));
            if (showDAO.create(movieId, theatreId, cinemaBuildingOwner.getId(), showtime)) {
                Optional<Show> showOpt = showDAO.get(movieId, theatreId, showtime);
                if (showOpt.isPresent()) {
                    out.println(gson.toJson(ResponseWrapper.get(showOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
                } else {
                    out.println(gson.toJson(ResponseWrapper.error("Error!")));
                }
            } else {
                out.println(gson.toJson(ResponseWrapper.error("Error! Hint : The show times may be overlapping")));
            }
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! The movie doesn't exist")));
        }

        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
