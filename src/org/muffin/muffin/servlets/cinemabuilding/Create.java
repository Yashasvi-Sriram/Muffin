package org.muffin.muffin.servlets.cinemabuilding;

import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.ValidRegionDAOImpl;
import org.muffin.muffin.daos.ValidRegionDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.MovieOwnerEnsuredSessionServlet;
import org.muffin.muffin.servlets.SessionKeys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
import org.muffin.muffin.daos.MovieDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * doGetWithSession:  tries to create a new movie with given params, if success returns created obj, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/cinemabuilding/create")
public class Create extends CinemaBuildingOwnerEnsuredSessionServlet {
    private CinemaBuildingDAO cinemaBuildingDAO = new CinemaBuildingDAOImpl();
    private ValidRegionDAO validRegionDAO = new ValidRegionDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String name = request.getParameter("name");
        String streetName = request.getParameter("streetName");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String country = request.getParameter("country");
        String zip = request.getParameter("zip");

        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        Optional<ValidRegion> validRegionOpt = validRegionDAO.get(city, state, country);
        if (validRegionOpt.isPresent()) {
            Optional<CinemaBuilding> cinemaBuildingOpt = cinemaBuildingDAO.create(cinemaBuildingOwner.getId(), name, streetName, city, state, country, zip);
            if (cinemaBuildingOpt.isPresent()) {
                out.println(gson.toJson(ResponseWrapper.get(cinemaBuildingOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
            } else {
                out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The building has to be different from all the existing ones")));
            }
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: Enter Valid Region(City,State,Country)")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
