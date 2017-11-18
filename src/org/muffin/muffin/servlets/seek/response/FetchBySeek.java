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

/**
 * doGetWithSession:  returns requested seek responses
 * doPostWithSession: same as get
 */
@WebServlet("/seek/response/fetch/seek")
public class FetchBySeek extends EnsuredSessionServlet {
    private SeekResponseDAO seekResponseDAO = new SeekResponseDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String lastSeen = request.getParameter("lastSeen");
        int muffId = Integer.parseInt(request.getParameter("id"));
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<SeekResponse> seekResponses = seekResponseDAO.getBySeek(muffId, offset, limit, Timestamp.valueOf(lastSeen));
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(seekResponses, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
