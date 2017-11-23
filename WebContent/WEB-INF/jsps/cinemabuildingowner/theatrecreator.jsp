<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Theatre Creator</jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionCinemaBuildingOwnerId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/threatrecreatorapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<TheatreCreatorApp
                            contextPath="${pageContext.request.contextPath}"
                            submitUrl="/cinemabuildingowner/theatrecreator"
                            cinemaBuildingId={${requestScope.cinemaBuilding.getId()}}
                    />, document.getElementById('app'));
                </script>
                <div class="container-fluid" style="min-height: 100vh">
                    <h1>Theatre Creator</h1>
                    <h3>${requestScope.cinemaBuilding.getName()}, ${requestScope.cinemaBuilding.getStreetName()}, ${requestScope.cinemaBuilding.getCity()}</h3>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>
    </jsp:body>
</m:base>