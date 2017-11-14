<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MOVIE_OWNER).name}  | Home</jsp:attribute>
    <jsp:body>
        <div class="container">
            <h1>Hello, ${sessionScope.get(SessionKeys.MOVIE_OWNER).name}</h1>
            <div class="collection">
                <a href="${pageContext.request.contextPath}/movieowner/movieeditor" class="collection-item">Movie Editor</a>
            </div>
        </div>
    </jsp:body>
</m:base>