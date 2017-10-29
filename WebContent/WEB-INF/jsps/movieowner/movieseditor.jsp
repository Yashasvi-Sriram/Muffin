<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:body>
        <table>
            <jstl:forEach items="${requestScope.movieOwnerList}" var="movieOwnerList">
                <tr>
                    <td><jstl:out value="${movieOwnerList.name}"/></td>
                    <td><jstl:out value="${movieOwnerList.durationInMinutes}"/></td>
                </tr>
            </jstl:forEach>
        </table>
    </jsp:body>
</m:base>