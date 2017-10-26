<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:userlogin>
    <jsp:attribute name="title">Movie Owner Title</jsp:attribute>
    <jsp:attribute name="bgColor">pink</jsp:attribute>
    <jsp:attribute name="action">${requestScope.get("action")}</jsp:attribute>
    <jsp:attribute name="handleValue">Hello</jsp:attribute>
    <jsp:attribute name="passwordValue">Hello</jsp:attribute>
</m:userlogin>
