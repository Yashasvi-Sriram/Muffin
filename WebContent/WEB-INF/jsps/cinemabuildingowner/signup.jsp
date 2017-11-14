<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Cinema Building Owner Signup</jsp:attribute>
    <jsp:body>
        <m:usersignup>
            <jsp:attribute name="message">${requestScope.get("message")}</jsp:attribute>
            <jsp:attribute name="bgColor">purple</jsp:attribute>
            <jsp:attribute name="action">${requestScope.get("action")}</jsp:attribute>
            <jsp:attribute name="nameValue">${requestScope.get("name")}</jsp:attribute>
            <jsp:attribute name="handleValue">${requestScope.get("handle")}</jsp:attribute>
            <jsp:attribute name="passwordValue">${requestScope.get("password")}</jsp:attribute>
        </m:usersignup>
    </jsp:body>
</m:base>