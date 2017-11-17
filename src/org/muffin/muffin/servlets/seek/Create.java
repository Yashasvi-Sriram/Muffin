package org.muffin.muffin.servlets.seek;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.muffin.muffin.beans.*;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * doGetWithSession:  tries to create a new seek with given params, if success returns created obj
 * doPostWithSession: same as GET
 */
@WebServlet("/seek/create")
public class Create extends MuffEnsuredSessionServlet {
    private SeekDAO seekDAO = new SeekDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        Gson gson = new GsonBuilder().create();
        String text = request.getParameter("text");
        List<Integer> genreIds = gson.fromJson(request.getParameter("genreIds"), new TypeToken<List<Integer>>() {
        }.getType());
        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        PrintWriter out = response.getWriter();
        if (seekDAO.create(muff.getId(), text, genreIds)) {
            out.println(gson.toJson(ResponseWrapper.get("Seek noted! You will be served soon", ResponseWrapper.STRING_RESPONSE)));
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
