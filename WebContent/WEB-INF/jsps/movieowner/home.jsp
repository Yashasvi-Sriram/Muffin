<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.movie_owner.name}  | Home</jsp:attribute>
    <jsp:body>
        <div class="container">
                ${pageContext.request.contextPath}
            Hello, ${sessionScope.movie_owner.name}
        </div>
        <a href="${pageContext.request.contextPath}/movieownermovieseditor">Movies</a>
    </jsp:body>
</m:base>