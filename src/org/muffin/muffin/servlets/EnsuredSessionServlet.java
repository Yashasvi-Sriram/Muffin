package org.muffin.muffin.servlets;

import com.google.gson.Gson;
import org.muffin.muffin.responsetypes.StringResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EnsuredSessionServlet extends HttpServlet {

    private boolean ensureSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession(false) == null) {
            PrintWriter out = response.getWriter();
            out.print(new Gson().toJson(StringResponse.error("invalid session")));
            out.close();
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (ensureSession(request, response)) {
            doGetWithSession(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (ensureSession(request, response)) {
            doPostWithSession(request, response);
        }
    }

    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
