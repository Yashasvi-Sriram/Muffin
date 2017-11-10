package org.muffin.muffin.servlets.theatre;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.CinemaBuilding;
import org.muffin.muffin.beans.CinemaBuildingOwner;
import org.muffin.muffin.beans.Theatre;
import org.muffin.muffin.daoimplementations.CinemaBuildingDAOImpl;
import org.muffin.muffin.daoimplementations.TheatreDAOImpl;
import org.muffin.muffin.daos.CinemaBuildingDAO;
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
import java.util.Optional;

/**
 * doGetWithSession:  tries to create a new theatre with given params, if success returns created obj, else returns error
 * doPostWithSession: same as GET
 */
@WebServlet("/theatre/delete")
public class Delete extends CinemaBuildingOwnerEnsuredSessionServlet {
    TheatreDAO theatreDAO = new TheatreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        Gson gson = new GsonBuilder().create();
        PrintWriter out = response.getWriter();
        if (theatreDAO.delete(id, cinemaBuildingOwner.getId())) {

            out.println(gson.toJson(ResponseWrapper.get(0, ResponseWrapper.NUMBER_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error!")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}