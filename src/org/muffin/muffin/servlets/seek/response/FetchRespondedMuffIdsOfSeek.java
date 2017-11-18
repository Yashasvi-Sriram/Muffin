package org.muffin.muffin.servlets.seek.response;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.daoimplementations.SeekResponseDAOImpl;
import org.muffin.muffin.daos.SeekResponseDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.EnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * doGetWithSession:  returns requested muff ids of all seek responses of a seek
 * doPostWithSession: same as get
 */
@WebServlet("/seek/response/fetch/respondedMuffIdsOfSeek")
public class FetchRespondedMuffIdsOfSeek extends EnsuredSessionServlet {
    private SeekResponseDAO seekResponseDAO = new SeekResponseDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        int seekId = Integer.parseInt(request.getParameter("seekId"));
        List<Integer> seekResponseMuffIds = seekResponseDAO.getMuffIdsOfAllSeekResponsesOfSeek(seekId);
        System.out.println(seekResponseMuffIds);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(seekResponseMuffIds, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
