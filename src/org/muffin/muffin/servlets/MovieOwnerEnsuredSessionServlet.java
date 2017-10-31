package org.muffin.muffin.servlets;

import com.google.gson.Gson;
import org.muffin.muffin.responses.ResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class MovieOwnerEnsuredSessionServlet extends EnsuredSessionServlet {

    @Override
    protected boolean ensureSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.MOVIE_OWNER) == null) {
            PrintWriter out = response.getWriter();
            out.print(new Gson().toJson(ResponseWrapper.error("invalid session")));
            out.close();
            return false;
        }
        return true;
    }
}
