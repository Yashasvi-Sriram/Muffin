<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Home</jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/infinitefeedapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<InfiniteFeedApp muffId={${sessionScope.get(SessionKeys.MUFF).getId()}}
                                                     contextPath="${pageContext.request.contextPath}"
                                                     reviewFetchUrl="/review/fetch/muff"/>, document.getElementById('infinite-feed-app'));
                </script>
                <%--Infinite Feed App--%>
                <div class="container">
                    <div id="infinite-feed-app"></div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>