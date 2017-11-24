package org.muffin.muffin.servlets.seek.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.muffin.muffin.beans.Muff;
import org.muffin.muffin.beans.Seek;
import org.muffin.muffin.beans.SeekResponse;
import org.muffin.muffin.daoimplementations.MuffDAOImpl;
import org.muffin.muffin.daoimplementations.SeekDAOImpl;
import org.muffin.muffin.daoimplementations.SeekResponseDAOImpl;
import org.muffin.muffin.daos.MuffDAO;
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
 * doGetWithSession:  toggles seek response approval
 * doPostWithSession: same as get
 */
@WebServlet("/seek/response/approval/toggle")
public class ToggleApproval extends MuffEnsuredSessionServlet {
    private MuffDAO muffDAO = new MuffDAOImpl();
    private SeekDAO seekDAO = new SeekDAOImpl();
    private SeekResponseDAO seekResponseDAO = new SeekResponseDAOImpl();

    @Override
    protected void doGetWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        int seekResponseId = Integer.parseInt(request.getParameter("seekResponseId"));
        Optional<SeekResponse> seekResponseOpt = seekResponseDAO.getById(seekResponseId);
        if (!seekResponseOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The seek response may not exist!")));
            out.close();
            return;
        }
        Optional<Seek> seekOpt = seekDAO.getById(seekResponseOpt.get().getSeekId());
        if (!seekOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: The seek may not exist!")));
            out.close();
            return;
        }
        // check whether the in session muff is the one who had given the seek
        Muff inSessionMuff = (Muff) session.getAttribute(SessionKeys.MUFF);
        if (seekOpt.get().getMuff().getId() != inSessionMuff.getId()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: You cannot toggle approval of this seek response!")));
            out.close();
            return;
        }
        Optional<Integer> approvalStatusOpt = seekResponseDAO.toggleApproval(seekResponseId);
        if (!approvalStatusOpt.isPresent()) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: Could not toggle approval!")));
            out.close();
            return;
        }
        int muffIdOfSeekResponse = seekResponseOpt.get().getMuff().getId();
        boolean muffNoApprovalsUpdated;
        if (approvalStatusOpt.get() == 1) {
            muffNoApprovalsUpdated = muffDAO.incrementNoApprovalsByOne(muffIdOfSeekResponse);
        } else {
            muffNoApprovalsUpdated = muffDAO.decrementNoApprovalsByOne(muffIdOfSeekResponse);
        }
        if (!muffNoApprovalsUpdated) {
            out.println(gson.toJson(ResponseWrapper.error("Error! Hint: Could not update muff no approvals!")));
            out.close();
            return;
        }
        out.println(gson.toJson(ResponseWrapper.get(approvalStatusOpt.get(), ResponseWrapper.NUMBER_RESPONSE)));
        out.close();
    }

    @Override
    protected void doPostWithSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        doGetWithSession(request, response, session);
    }
}

