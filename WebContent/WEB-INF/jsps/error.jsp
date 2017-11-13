<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">
      Muffin
    </jsp:attribute>
    <jsp:body>
        <div class="container">
            <div class="flow-text">
                <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="Muffin"/>
                M<span class="grey-text">ovie</span>
                <span class="grey-text">B</span>uff
                in<span class="grey-text">c.</span>
            </div>
            <blockquote>
                <h4><code>
                    Something funny happened there!<br>
                    It might be that: <br>${requestScope.message}</code></h4>
            </blockquote>
        </div>
    </jsp:body>
</m:base>