package org.muffin.muffin.servlets.muff;

import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daos.MuffDAO;
import org.muffin.muffin.servlets.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * doGet: renders muff login page
 * doPost: if auth success redirect to muff home, else print error in login page
 */
@WebServlet("/muff/login")
public class Login extends HttpServlet {
    MuffDAO muffDAO = new MuffDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", request.getContextPath() + "/muff/login");
        request.getRequestDispatcher("/WEB-INF/jsps/muff/login.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String handle = request.getParameter("handle");
        String password = request.getParameter("password");
        if (muffDAO.exists(handle, password)) {
            // If session already exists remove invalidate it
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            Optional<Muff> muffOpt = muffDAO.get(handle);
            muffOpt.ifPresent(muff -> newSession.setAttribute(SessionKeys.MUFF, muff));
            response.sendRedirect(request.getContextPath() + "/muff/home");
        } else {
            request.setAttribute("message", "Error! Hint: Check Credentials");
            request.setAttribute("action", request.getContextPath() + "/muff/login");
            request.setAttribute("handle", handle);
            request.setAttribute("password", password);
            request.getRequestDispatcher("/WEB-INF/jsps/muff/login.jsp").include(request, response);
        }
    }
}
