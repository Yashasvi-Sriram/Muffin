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
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/infinitefeedapp.jsx"></script>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/togglefollowsapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<InfiniteFeedApp muffId={${requestScope.profileMuff.getId()}}
                                                     contextPath="${pageContext.request.contextPath}"
                                                     reviewFetchUrl="/review/fetch/muff"/>, document.getElementById('infinite-feed-app'));
                    ReactDOM.render(<ToggleFollowsApp
                            followerId={${sessionScope.get(SessionKeys.MUFF).getId()}}
                            followeeId={${requestScope.profileMuff.getId()}}
                            contextPath="${pageContext.request.contextPath}"
                            toggleFollowsUrl='/muff/follows/toggle'
                            doesFollowsUrl='/muff/follows/does'/>, document.getElementById('toggle-follows-app'));
                </script>
                <%--Infinite Feed App--%>
                <div class="row">
                    <div class="col s6 offset-s3">
                        <div id="infinite-feed-app"></div>
                    </div>
                </div>
                <div style="position: fixed; top: 0; left: 0; width: 22vw; height: 100vh;">
                    <div id="toggle-follows-app"></div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>