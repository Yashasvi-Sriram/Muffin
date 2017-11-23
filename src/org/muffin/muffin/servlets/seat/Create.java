package org.muffin.muffin.servlets.seat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.muffin.muffin.daoimplementations.SeatDAOImpl;
import org.muffin.muffin.daos.SeatDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.CinemaBuildingOwnerEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/seat/create")
public class Create extends CinemaBuildingOwnerEnsuredSessionServlet {
    private SeatDAO seatDAO = new SeatDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        super.doGetWithSession(request, response, session);

        Gson gson = new GsonBuilder().create();
        int theatreID = Integer.parseInt(request.getParameter("theatreID"));
        /* Set up x and y */
        List<Integer> xyCombined = gson.fromJson(request.getParameter("seatsCoordinatesList"), new TypeToken<List<Integer>>() {
        }.getType());
        List<Integer> x = new ArrayList<Integer>();
        List<Integer> y = new ArrayList<Integer>();
        for(int i=0;i < xyCombined.size();i++) {
            if(i % 2 == 0) x.add(xyCombined.get(i));
            else y.add(xyCombined.get(i));
        }
        PrintWriter out = response.getWriter();
        if(xyCombined.size() % 2 == 0) {
            if (seatDAO.createList(theatreID, x, y)) {
            /* Upon Success */
                out.println(gson.toJson(ResponseWrapper.get(true, ResponseWrapper.BOOLEAN_RESPONSE)));
            } else {
                out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The coordinates of seats should be unique and not already present !")));
            }
        }
        else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: Data of seat coordinates might not be passed correctly !")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        super.doPostWithSession(request, response, session);
        doGetWithSession(request, response, session);
    }
}
