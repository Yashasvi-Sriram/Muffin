package org.muffin.muffin.servlets.muff;

import org.muffin.muffin.servlets.MuffEnsuredSessionServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * doGetWithSession:  renders muff home page
 * doPostWithSession: same as get
 */
@WebServlet("/muff/home")
public class Home extends MuffEnsuredSessionServlet {
    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsps/muff/home.jsp").include(request, response);
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
