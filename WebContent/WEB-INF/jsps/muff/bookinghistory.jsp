<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name}'s Booking History</jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
            <jsp:body>
                <div class="container" style="min-height: 100vh">
                    <h1>${sessionScope.get(SessionKeys.MUFF).name}'s Booking History</h1>
                    <div class="collection">
                        <jstl:forEach items="${requestScope.bookingHistory}" var="booking">
                            <div class="collection-item">
                                    ${booking.getMovieName()}
                                <span class="secondary-content">
                                        ${booking.getBookedOn()}
                                </span>
                                <div class="collection">
                                    <jstl:forEach items="${booking.getBookedShowSeats()}" var="seat">
                                        <div class="collection-item">
                                                Row: ${seat.getY() + 1}, Column: ${seat.getX() + 1}
                                        </div>
                                    </jstl:forEach>
                                </div>
                            </div>
                        </jstl:forEach>
                    </div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>