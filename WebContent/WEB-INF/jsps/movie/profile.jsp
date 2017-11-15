<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Movie Details</jsp:attribute>
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
                        src="${pageContext.request.contextPath}/static/muffinjs/infinitemoviefeedapp.jsx"></script>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/moviestatsapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<InfiniteMovieFeedApp movieId={${requestScope.profileMovie.getId()}}
                                                     contextPath="${pageContext.request.contextPath}"
                                                     reviewFetchUrl="/review/fetch/movie"/>, document.getElementById('infinite-feed-app'));

                </script>
                <script type="text/babel">
                    ReactDOM.render(<MovieStatsApp movieId={${requestScope.profileMovie.getId()}}
                                                          contextPath="${pageContext.request.contextPath}"
                                                          statsFetchUrl="/movie/stats"/>, document.getElementById('movie-stats-app'));

                </script>
                <%--Infinite Feed App--%>
                <div class="row">
                    <div class="col s4 center-align">
                        <h3>${requestScope.profileMovie.getName()}'s Profile</h3>
                        <br>
                        <div id="movie-stats-app"></div>
                    </div>
                    <div class="col s4">
                        <div id="infinite-feed-app"></div>
                    </div>
                </div>

            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>