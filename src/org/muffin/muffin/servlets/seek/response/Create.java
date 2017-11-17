package org.muffin.muffin.servlets.seek.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Movie;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seek;
import org.muffin.muffin.beans.SeekResponse;
import org.muffin.muffin.daoimplementations.MovieDAOImpl;
import org.muffin.muffin.daoimplementations.SeekDAOImpl;
import org.muffin.muffin.daoimplementations.SeekResponseDAOImpl;
import org.muffin.muffin.daos.MovieDAO;
import org.muffin.muffin.daos.SeekDAO;
import org.muffin.muffin.daos.SeekResponseDAO;
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
 * doGetWithSession:  tries to create a new seek response with given params, if success returns created obj
 * doPostWithSession: same as GET
 * todo: reduce db activity if possible
 */
@WebServlet("/seek/response/create")
public class Create extends MuffEnsuredSessionServlet {
    private SeekDAO seekDAO = new SeekDAOImpl();
    private SeekResponseDAO seekResponseDAO = new SeekResponseDAOImpl();
    private MovieDAO movieDAO = new MovieDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();

        String movieName = request.getParameter("movieName");
        Optional<Movie> movieOpt = movieDAO.get(movieName);
        // if movie does not exist
        if (!movieOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The Movie does not exist")));
            out.close();
            return;
        }
        int movieId = movieOpt.get().getId();
        Muff muff = (Muff) session.getAttribute(SessionKeys.MUFF);
        int seekId = Integer.parseInt(request.getParameter("seekId"));
        Optional<Seek> seekOpt = seekDAO.getById(seekId);
        if (!seekOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The seek may be deleted!")));
            out.close();
            return;
        }
        if (seekOpt.get().getMuff().getId() == muff.getId()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: You cannot recommend movies to yourself")));
            out.close();
            return;
        }
        String text = request.getParameter("text");
        Optional<SeekResponse> seekResponseOpt = seekResponseDAO.create(muff.getId(), seekId, movieId, text);
        if (seekResponseOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.get(seekResponseOpt.get(), ResponseWrapper.OBJECT_RESPONSE)));
        } else {
            out.println(gson.toJson(ResponseWrapper.error("Error! You may already have recommended to the seek")));
        }
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}
