package org.muffin.muffin.servlets.muff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.responses.ResponseWrapper;
import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;
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
 * doGetWithSession:  returns no approvals
 * doPostWithSession: same as get
 */
@WebServlet("/muff/fetch/noapprovals")
public class FetchNoApprovals extends MuffEnsuredSessionServlet {
    private MuffDAO muffDAO = new MuffDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        Optional<Integer> noFollowersOpt = muffDAO.getNoApprovals(muff.getId());
        if (noFollowersOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(noFollowersOpt.get(), ResponseWrapper.NUMBER_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The user may not be found")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

