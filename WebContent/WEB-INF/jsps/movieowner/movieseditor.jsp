<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:body>
        <table>
		  <c:forEach items="${requestScope.movieOwnerList}" var="movieOwnerList">
		    <tr>
		      <td><c:out value="${movieOwnerList.name}" /></td>
		      <td><c:out value="${movieOwnerList.durationInMinutes}" /></td>
		    </tr>
		  </c:forEach>
	</table>
    </jsp:body>
</m:base>