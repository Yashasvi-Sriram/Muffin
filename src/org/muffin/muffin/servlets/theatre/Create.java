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
@WebServlet("/theatre/create")
public class Create extends CinemaBuildingOwnerEnsuredSessionServlet {
    private TheatreDAO theatreDAO = new TheatreDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        int cinemaBuildingId = Integer.parseInt(request.getParameter("cinemaBuildingId"));
        int screenNo = Integer.parseInt(request.getParameter("screenNo"));
        CinemaBuildingOwner cinemaBuildingOwner = (CinemaBuildingOwner) session.getAttribute(SessionKeys.CINEMA_BUILDING_OWNER);
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        if (theatreDAO.create(cinemaBuildingId, screenNo, cinemaBuildingOwner.getId()).isPresent()) {
            Optional<Theatre> theatreOpt = theatreDAO.get(cinemaBuildingId, screenNo);
            if (theatreOpt.isPresent()) {
                out.println(gson.toJson(ResponseWrapper.get(theatreOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
            } else {
                System.out.println("Critical error!");
                out.println(gson.toJson(ResponseWrapper.error("Error!")));
            }
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The Screen Number has to be different from all the existing ones")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
