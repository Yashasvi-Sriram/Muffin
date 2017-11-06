package org.muffin.muffin.servlets.muff;

import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * doGetWithSession:  returns matched muffs
 * doPostWithSession: same as get
 */
@WebServlet("/muff/search")
public class Search extends MuffEnsuredSessionServlet {
    MuffDAO muffDAO = new MuffDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String pattern = request.getParameter("pattern");
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        List<Muff> muffs = muffDAO.search(pattern, offset, limit);
        PrintWriter out = response.getWriter();
        out.println(new GsonBuilder().create().toJson(ResponseWrapper.get(muffs, ResponseWrapper.ARRAY_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

