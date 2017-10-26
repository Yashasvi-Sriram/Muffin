package org.muffin.muffin.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/MovieOwnerLogin")
public class MovieOwnerLogin extends HttpServlet {
    private static final String HANDLE_FORM_NAME = "handle";
    private static final String PASSWORD_FORM_NAME = "password";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("action", "/MovieOwnerLogin");
        request.getRequestDispatcher("WEB-INF/jsps/movie_owner_login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
