package org.muffin.muffin.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/Home")
public class Home extends EnsuredSessionServlet {
    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("Hello");
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGetWithSession(request, response);
    }
}
