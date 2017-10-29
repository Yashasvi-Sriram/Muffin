<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Owner Login</jsp:attribute>
    <jsp:body>
        <m:userlogin>
            <jsp:attribute name="message">${requestScope.get("message")}</jsp:attribute>
            <jsp:attribute name="bgColor">blue</jsp:attribute>
            <jsp:attribute name="action">${requestScope.get("action")}</jsp:attribute>
            <jsp:attribute name="handleValue">${requestScope.get("handle")}</jsp:attribute>
            <jsp:attribute name="passwordValue">${requestScope.get("password")}</jsp:attribute>
        </m:userlogin>
    </jsp:body>
</m:base>