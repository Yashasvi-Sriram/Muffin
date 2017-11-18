package org.muffin.muffin.servlets.seek.response;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.SeekResponse;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  returns requested seek responses
 * doPostWithSession: same as get
 */
@WebServlet("/seek/response/check/forNew")
public class CheckForNew extends EnsuredSessionServlet {
    private SeekResponseDAO seekResponseDAO = new SeekResponseDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String lastSeen = request.getParameter("lastSeen");
        int seekId = Integer.parseInt(request.getParameter("seekId"));
        Optional<Boolean> newResponsesExist = seekResponseDAO.checkForNewResponsesOfSeek(seekId, Timestamp.valueOf(lastSeen));
        PrintWriter out = response.getWriter();
        if (newResponsesExist.isPresent()) {
            out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(newResponsesExist.get(), ResponseWrapper.BOOLEAN_RESPONSE)));
        } else {
            out.println(new GsonBuilder().create().toJson(ResponseWrapper.error("The seek with given id may not exist!")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
