package org.muffin.muffin.servlets.muff;

import lombok.NonNull;
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
 * doGet: renders muff signup page
 * doPost: if signup success redirect to muff login, else print error in signup page
 */
@WebServlet("/muff/signup")
public class Signup extends HttpServlet {
    private MuffDAO muffDAO = new MuffDAOImpl();

    private boolean isValid(String s) {
        return s != null && s.length() != 0 && s.length() <= 50;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", request.getContextPath() + "/muff/signup");
        request.getRequestDispatcher("/WEB-INF/jsps/muff/signup.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String handle = request.getParameter("handle");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        if (!(isValid(handle) && isValid(name) && isValid(password))) {
            request.setAttribute("message", "Error! Hint: Please fill all the fields!");
        } else if (!muffDAO.create(handle, name, password).isPresent()) {
            request.setAttribute("message", "Error! Hint: Handle already exists!");
        } else {
            response.sendRedirect(request.getContextPath() + "/muff/login");
            return;
        }
        request.setAttribute("action", request.getContextPath() + "/muff/signup");
        request.setAttribute("handle", handle);
        request.setAttribute("name", name);
        request.setAttribute("password", password);
        request.getRequestDispatcher("/WEB-INF/jsps/muff/signup.jsp").include(request, response);
    }
}
