<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Select Seats</jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/bookingapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<BookingApp
                            theatre={${requestScope.theatre}}
                            showId={${requestScope.showId}}
                            alreadyBookedSeats={${requestScope.alreadyBookedSeats}}
                            theatreSeats={${requestScope.theatreSeats}}
                    />, document.getElementById('app'));
                </script>
                <h2>Select Seats</h2>
                <div id="app" style="min-height: 100vh"></div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>