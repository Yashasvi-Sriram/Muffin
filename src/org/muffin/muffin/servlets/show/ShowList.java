package org.muffin.muffin.servlets.show;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.ShowDAOImpl;
import org.muffin.muffin.daoimplementations.ValidRegionDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.ShowDAO;
import org.muffin.muffin.daos.ValidRegionDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.*;


/**
 * doGetWithSession:  returns matched shows
 * doPostWithSession: same as get
 */
@WebServlet("/show/showlist")
public class ShowList extends EnsuredSessionServlet {
    private ShowDAO showDAO = new ShowDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        MovieDAO movieDAO = new MovieDAOImpl();
        ValidRegionDAO validRegionDAO = new ValidRegionDAOImpl();
        String moviename = request.getParameter("movieName");
        String regionName = request.getParameter("regionName");
        String regionAttr[] = regionName.split(",");
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (regionAttr.length != 3) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Invalid region entry")));
            out.close();
            return;

        }
        Optional<Movie> MovieOpt = movieDAO.get(moviename);

        if (!MovieOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Invalid Movie Name")));
            out.close();
            return;

        }


        Optional<ValidRegion> ValidRegionOpt = validRegionDAO.get(regionAttr[0], regionAttr[1], regionAttr[2]);

        if (!ValidRegionOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Invalid region entry")));
            out.close();
            return;

        }

        String startTimeStampString = request.getParameter("startTimeStamp");
        String endTimeStampString = request.getParameter("endTimeStamp");

        LocalDateTime startTimeStamp = LocalDateTime.parse(startTimeStampString);
        LocalDateTime endTimeStamp = LocalDateTime.parse(endTimeStampString);
        Showtime showtime = new Showtime(startTimeStamp, endTimeStamp);
        Map<CinemaBuilding, List<Show>> allShows = showDAO.getAllShows(MovieOpt.get().getId(), regionAttr[0], regionAttr[1], regionAttr[2], showtime);
        List<Pair<CinemaBuilding, List<Show>>> allShowsList = new ArrayList<>();
        for (Map.Entry<CinemaBuilding, List<Show>> entry : allShows.entrySet()) {
            allShowsList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(allShowsList, ResponseWrapper.ARRAY_RESPONSE)));


        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}



