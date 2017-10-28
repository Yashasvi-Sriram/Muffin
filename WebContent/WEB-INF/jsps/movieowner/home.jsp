<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Owner Home</jsp:attribute>
    <jsp:body>
        <div class="container">
            Hello, ${requestScope.movieOwner.name}
        </div>
        <a href="/movieownermovieseditor">Movies</a>
    </jsp:body>
</m:base>