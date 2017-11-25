package org.muffin.muffin.servlets.seek;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.daoimplementations.SeekDAOImpl;
import org.muffin.muffin.daos.SeekDAO;
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
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  tries to fetch automated response of a seek
 * doPostWithSession: same as GET
 */
@WebServlet("/seek/fetch/automatedresponse")
public class FetchAutomatedResponse extends MuffEnsuredSessionServlet {
    private SeekDAO seekDAO = new SeekDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Gson gson = new GsonBuilder().create();
        PrintWriter out = response.getWriter();
        int seekId = Integer.parseInt(request.getParameter("seekId"));
        Optional<Movie> movieOpt = seekDAO.getAutomatedSuggestion(seekId);
        if (movieOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(movieOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error!")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
