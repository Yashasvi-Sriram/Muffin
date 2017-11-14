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
            <%-- Log out button --%>
            <a title="Log out"
               href="${pageContext.request.contextPath}/movieowner/logout"
               class="btn-floating btn-large waves-effect waves-light red modal-trigger"
               style="position:fixed;bottom:20px; right: 20px">
                <i class="material-icons">power_settings_new</i>
            </a>
        </div>
    </jsp:body>
</m:base>