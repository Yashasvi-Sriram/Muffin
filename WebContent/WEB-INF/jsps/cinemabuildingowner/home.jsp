<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute
            name="title">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).name}  | Cinema Building Owner Home</jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionMuffId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
            <jsp:body>
                <div class="container" style="min-height: 100vh">
                    <h1>Hello, ${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getName()}</h1>
                    <div class="collection">
                        <a href="${pageContext.request.contextPath}/cinemabuildingowner/buildinglist"
                           class="collection-item">Your Cinemas</a>
                    </div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>
    </jsp:body>
</m:base>