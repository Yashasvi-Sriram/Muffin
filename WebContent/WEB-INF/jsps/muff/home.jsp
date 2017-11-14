<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Home</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #infinite-feed-app {
                margin-bottom: 50vh;
            }

            #infinite-feed-app .review {
                background-color: whitesmoke;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/infinitefeedapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<InfiniteFeedApp muffId={${sessionScope.get(SessionKeys.MUFF).getId()}}
                                                     contextPath="${pageContext.request.contextPath}"
                                                     reviewFetchUrl="/review/fetch/followers"/>, document.getElementById('infinite-feed-app'));
                </script>
                <div class="section row">
                    <div class="col s4 center-align"><h3>Hello, ${sessionScope.get(SessionKeys.MUFF).getName()}</h3></div>
                    <div class="col s4 ">
                            <%--Infinite Feed App--%>
                        <div id="infinite-feed-app"></div>
                    </div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>