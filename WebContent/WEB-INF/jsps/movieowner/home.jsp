<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MOVIE_OWNER).name}  | Home</jsp:attribute>
    <jsp:body>
        <m:insessionmovieownercommons>
            <jsp:attribute
                    name="inSessionMovieOwnerId">${sessionScope.get(SessionKeys.MOVIE_OWNER).getId()}</jsp:attribute>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:body>
                <div class="container" style="min-height: 100vh">
                    <h1>Hello, ${sessionScope.get(SessionKeys.MOVIE_OWNER).getName()}</h1>
                    <div class="collection">
                        <a href="${pageContext.request.contextPath}/movieowner/movieeditor" class="collection-item">Movie
                            Editor</a>
                    </div>
                </div>
            </jsp:body>
        </m:insessionmovieownercommons>
    </jsp:body>
</m:base>