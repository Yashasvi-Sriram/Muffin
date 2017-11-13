package org.muffin.muffin.servlets.validregion;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.ValidRegion;
import org.muffin.muffin.daoimplementations.ValidRegionDAOImpl;
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
import java.util.List;


/**
 * doGetWithSession:  returns matched regions
 * doPostWithSession: same as get
 */
@WebServlet("/validregion/search")
public class Search extends EnsuredSessionServlet {
    private ValidRegionDAO validRegionDAO = new ValidRegionDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String pattern = request.getParameter("pattern");
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<ValidRegion> validRegions = validRegionDAO.search(pattern, offset, limit);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(validRegions, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

