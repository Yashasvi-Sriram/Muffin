<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Owner Home</jsp:attribute>
    <jsp:body>
        ${requestScope.movies[0].id}
    </jsp:body>
</m:base>